package com.bookmyturf.service.implementation;

import com.bookmyturf.constraints.BookingStatus;
import com.bookmyturf.constraints.SlotStatus;
import com.bookmyturf.entity.Booking;
import com.bookmyturf.entity.BookingSlot;
import com.bookmyturf.entity.SlotTiming;
import com.bookmyturf.entity.User;
import com.bookmyturf.jparepository.BookingRepository;
import com.bookmyturf.jparepository.BookingSlotRepository;
import com.bookmyturf.jparepository.SlotTimingRepository;
import com.bookmyturf.jparepository.UserJpaRepository;
import com.bookmyturf.service.BookingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingSlotRepository bookingSlotRepository;
    private final SlotTimingRepository slotTimingRepository;
    private final UserJpaRepository userRepository;
    @Override
    @Transactional
    public Booking createBooking(Long userId, List<Long> slotTimingIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // check all slots are available
        List<SlotTiming> slots = slotTimingRepository.findAllById(slotTimingIds);
        for (SlotTiming slot : slots) {
            Optional<BookingSlot> activeSlot = bookingSlotRepository.findActiveSlot(slot.getId());
            if (activeSlot.isPresent()) {
                throw new RuntimeException("Slot already booked or temporarily blocked");
            }
        }
        double totalAmount = slots.stream()
                .mapToDouble(SlotTiming::getPrice)
                .sum();
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setStatus(BookingStatus.PENDING);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        booking.setTotalAmount(totalAmount);
        booking = bookingRepository.save(booking);

        for (SlotTiming slot : slots) {
            BookingSlot bs = new BookingSlot();
            bs.setBooking(booking);
            bs.setSlotTiming(slot);
            bs.setStatus(SlotStatus.TEMP_BLOCKED);
            bs.setLockedAt(LocalDateTime.now());
            bookingSlotRepository.save(bs);
        }

        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() != BookingStatus.PENDING)
            throw new RuntimeException("Booking already confirmed or expired");

        booking.setStatus(BookingStatus.CONFIRMED);
        booking.getBookingSlots().forEach(slot -> slot.setStatus(SlotStatus.BOOKED));
        return bookingRepository.save(booking);
    }


    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(BookingStatus.CANCELLED);
        booking.getBookingSlots().forEach(slot -> slot.setStatus(SlotStatus.AVAILABLE));
        return bookingRepository.save(booking);
    }


    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expireBookings() {
        List<Booking> pendingBookings = bookingRepository.findByStatus(BookingStatus.PENDING);

        LocalDateTime now = LocalDateTime.now();
        for (Booking booking : pendingBookings) {
            if (booking.getExpiresAt().isBefore(now)) {
                booking.setStatus(BookingStatus.EXPIRED);
                booking.getBookingSlots().forEach(slot -> slot.setStatus(SlotStatus.AVAILABLE));
                bookingRepository.save(booking);
            }
        }
    }


    public List<SlotTiming> getAvailableSlots(Long sportId) {
        return slotTimingRepository.findAvailableSlots(sportId);
    }
}