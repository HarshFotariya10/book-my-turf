package com.bookmyturf.jparepository;

import com.bookmyturf.constraints.BookingStatus;
import com.bookmyturf.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByStatus(BookingStatus status);


    @Query("SELECT SUM(b.totalAmount) FROM Booking b " +
            "WHERE b.user.id = :userId AND b.status = 'CONFIRMED'")
    Double getTotalSales(Long userId);

    @Query("SELECT SUM(b.totalAmount) FROM Booking b " +
            "WHERE b.user.id = :userId AND b.status = 'CONFIRMED' " +
            "AND MONTH(b.createdAt) = MONTH(CURRENT_DATE) " +
            "AND YEAR(b.createdAt) = YEAR(CURRENT_DATE)")
    Double getCurrentMonthSales(Long userId);

    @Query("SELECT SUM(b.totalAmount) FROM Booking b " +
            "WHERE b.user.id = :userId AND b.status = 'CONFIRMED' " +
            "AND YEAR(b.createdAt) = YEAR(CURRENT_DATE)")
    Double getYearlySales(Long userId);


    // ********* Slot Count for ₹5 Charge *********

    @Query("SELECT COUNT(b.id) FROM Booking b " +
            "WHERE b.user.id = :userId AND b.status = 'CONFIRMED' " +
            "AND MONTH(b.createdAt) = MONTH(CURRENT_DATE) " +
            "AND YEAR(b.createdAt) = YEAR(CURRENT_DATE)")
    Long getCurrentMonthSlotCount(Long userId);

    @Query("SELECT COUNT(b.id) FROM Booking b " +
            "WHERE b.user.id = :userId AND b.status = 'CONFIRMED' " +
            "AND YEAR(b.createdAt) = YEAR(CURRENT_DATE)")
    Long getYearlySlotCount(Long userId);

    @Query("SELECT DISTINCT b " +
            "FROM Booking b " +
            "JOIN b.bookingSlots bs " +
            "JOIN bs.slotTiming st " +
            "JOIN st.sports s " +
            "JOIN s.location l " +
            "WHERE l.admin.id = :adminId " +
            "AND b.status = :status")
    List<Booking> findConfirmedBookingsByAdmin(
            @Param("adminId") Long adminId,
            @Param("status") BookingStatus status
    );

    @Query("SELECT b FROM Booking b WHERE b.status = :status")
    List<Booking> findAllByStatus(BookingStatus status);
}