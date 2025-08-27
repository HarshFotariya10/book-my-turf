package com.bookmyturf.service.implementation;

import com.bookmyturf.entity.*;
import com.bookmyturf.jparepository.CategoryRepository;
import com.bookmyturf.jparepository.LocationRepository;
import com.bookmyturf.jparepository.SportsRepository;
import com.bookmyturf.models.CreateSportRequest;
import com.bookmyturf.models.SportResponseDTO;
import com.bookmyturf.service.SportsService;
import com.bookmyturf.utils.FileStorageUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SportsServiceImpl implements SportsService {

    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final SportsRepository sportsRepository;

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPER_ADMIN')")
    public SportResponseDTO createSport(Long locationId, CreateSportRequest dto) {


        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        Sports sport = new Sports();
        sport.setName(dto.getName());
        sport.setDescription(dto.getDescription());
        sport.setCategory(category);
        sport.setLocation(location);

        List<MediaFile> mediaFiles = new ArrayList<>();
        if (dto.getMediaFiles() != null) {
            for (CreateSportRequest.MediaFileDTO mediaDTO : dto.getMediaFiles()) {
                MediaFile media = new MediaFile();
                media.setFileType(mediaDTO.getFileType());
                try {
                    String path = FileStorageUtil.saveBase64ToFile(mediaDTO.getBase64Data(), "uploads/sports-media");
                    media.setPath(path);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to save media file", e);
                }
                media.setSports(sport);
                mediaFiles.add(media);
            }
        }
        sport.setMediaFiles(mediaFiles);
        List<SlotTiming> slotTimings = new ArrayList<>();
        if (dto.getDaySlots() != null) {
            for (CreateSportRequest.DaySlotDTO daySlot : dto.getDaySlots()) {
                DayOfWeek day = DayOfWeek.valueOf(daySlot.getDay());

                // Preprocess prices per hour
                Double[] prices = new Double[24];
                if (daySlot.getSlots() != null) {
                    for (CreateSportRequest.TimeSlotDTO slot : daySlot.getSlots()) {
                        for (int h = slot.getStartHour(); h < slot.getEndHour(); h++) {
                            if (h >= daySlot.getOpenHour() && h < daySlot.getCloseHour()) {
                                prices[h] = slot.getPrice();
                            }
                        }
                    }
                }
                for (int hour = daySlot.getOpenHour(); hour < daySlot.getCloseHour(); hour++) {
                    SlotTiming slotTiming = new SlotTiming();
                    slotTiming.setDayOfWeek(day);
                    slotTiming.setStartTime(LocalTime.of(hour, 0));
                    slotTiming.setEndTime(LocalTime.of((hour + 1) % 24, 0));
                    slotTiming.setPrice(prices[hour]); // null if no price provided
                    slotTiming.setSports(sport);
                    slotTimings.add(slotTiming);
                }
            }
        }
        sport.setSlotTimings(slotTimings);
        sportsRepository.save(sport);
        return toDTO(sport);
    }
    public SportResponseDTO toDTO(Sports sport) {
        SportResponseDTO dto   = new SportResponseDTO();
        dto.setId(sport.getId());
        dto.setName(sport.getName());
        dto.setDescription(sport.getDescription());
        dto.setCategoryId(sport.getCategory().getId());
        dto.setCategoryName(sport.getCategory().getName());
        dto.setLocationId(sport.getLocation().getId());
        dto.setLocationName(sport.getLocation().getName());

        List<SportResponseDTO.MediaFileDTO> mediaList = sport.getMediaFiles().stream().map(m -> {
            SportResponseDTO.MediaFileDTO mf = new SportResponseDTO.MediaFileDTO();
            mf.setFileType(m.getFileType());
            mf.setPath(m.getPath());
            return mf;
        }).toList();
        dto.setMediaFiles(mediaList);

        return dto;
    }

}