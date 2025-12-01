    package com.bookmyturf.service.implementation;

    import com.bookmyturf.constraints.SlotStatus;
    import com.bookmyturf.entity.*;
    import com.bookmyturf.jparepository.BookingSlotRepository;
    import com.bookmyturf.jparepository.CategoryRepository;
    import com.bookmyturf.jparepository.LocationRepository;
    import com.bookmyturf.jparepository.SportsRepository;
    import com.bookmyturf.models.*;
    import com.bookmyturf.service.SportsService;
    import com.bookmyturf.utils.FileStorageUtil;
    import jakarta.persistence.EntityNotFoundException;
    import lombok.RequiredArgsConstructor;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.stereotype.Service;

    import java.io.IOException;
    import java.time.DayOfWeek;
    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.time.LocalTime;
    import java.util.ArrayList;
    import java.util.Comparator;
    import java.util.List;
    import java.util.Objects;
    import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    public class SportsServiceImpl implements SportsService {

        private final LocationRepository locationRepository;
        private final CategoryRepository categoryRepository;
        private final SportsRepository sportsRepository;
        private final BookingSlotRepository bookingSlotRepository;

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
            List<SportResponseDTO.MediaFileDTO> mediaList = new ArrayList<>();
            if (sport.getMediaFiles() != null) {
                for (MediaFile media : sport.getMediaFiles()) {
                    SportResponseDTO.MediaFileDTO mediaDTO = new SportResponseDTO.MediaFileDTO();
                    mediaDTO.setFileType(media.getFileType());
                    try {
                        String base64 = FileStorageUtil.convertFileToBase64(media.getPath());
                        mediaDTO.setBase64Data(base64);
                    } catch (IOException e) {
                        mediaDTO.setBase64Data(null);
                    }
                    mediaList.add(mediaDTO);
                }
            }
            dto.setMediaFiles(mediaList);

            return dto;
        }


        @Override
        public List<CategoryResponseDTO> getCategoriesByCity(String city) {
            if (city == null || city.isBlank()) {
                throw new IllegalArgumentException("City name must be provided");
            }

            // Normalize city name (case-insensitive search)
            String normalizedCity = city.trim().toLowerCase();

            // Fetch all categories for that city (case-insensitive)
            List<Category> categories = categoryRepository.findDistinctByCityIgnoreCase(normalizedCity);

            // Remove duplicates by category name (case-insensitive)
            return categories.stream()
                    .collect(Collectors.collectingAndThen(
                            Collectors.toMap(
                                    c -> c.getName().toLowerCase(),  // key = lowercase name
                                    c -> new CategoryResponseDTO(c.getId(), c.getName()),
                                    (existing, replacement) -> existing // if duplicate, keep first
                            ),
                            map -> new ArrayList<>(map.values())
                    ));
        }

        @Override
        public List<SportResponseDTO> getSportsByCityAndCategory(String city, String categoryName) {
            if (city == null || categoryName == null || city.isBlank() || categoryName.isBlank()) {
                throw new IllegalArgumentException("City and category name must be provided");
            }

            List<Sports> sportsList = sportsRepository.findByCityAndCategoryIgnoreCase(city, categoryName);

            return sportsList.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        }
        @Override
        public List<SportResponseDTO> filterSports(SportsFilterRequest filterRequest) {
            List<Sports> allSports = sportsRepository.findAll();

            return allSports.stream()
                    .filter(sport -> filterByCity(sport, filterRequest.getCity()))
                    .filter(sport -> filterByCategory(sport, filterRequest.getCategories()))
                    .filter(sport -> filterBySlot(sport, filterRequest.getSlots()))
                    .filter(sport -> filterByPrice(sport, filterRequest.getMinPrice(), filterRequest.getMaxPrice()))
                    .map(this::toDTOWithMinPrice)
                    .collect(Collectors.toList());
        }

        private boolean filterByCity(Sports sport, String city) {
            if (city == null || city.isBlank()) return true;
            return sport.getLocation() != null && city.equalsIgnoreCase(sport.getLocation().getCity());
        }

        private boolean filterByCategory(Sports sport, List<String> categories) {
            if (categories == null || categories.isEmpty()) return true;
            return sport.getCategory() != null && categories.stream()
                    .anyMatch(cat -> cat.equalsIgnoreCase(sport.getCategory().getName()));
        }

        private boolean filterBySlot(Sports sport, List<String> slots) {
            if (slots == null || slots.isEmpty()) return true;
            if (sport.getSlotTimings() == null || sport.getSlotTimings().isEmpty()) return false;

            return sport.getSlotTimings().stream().anyMatch(slot -> {
                int hour = slot.getStartTime().getHour();
                for (String s : slots) {
                    switch (s.toLowerCase()) {
                        case "morning" -> { if (hour >= 6 && hour < 12) return true; }
                        case "afternoon" -> { if (hour >= 12 && hour < 17) return true; }
                        case "evening" -> { if (hour >= 17 && hour <= 22) return true; }
                    }
                }
                return false;
            });
        }

        private boolean filterByPrice(Sports sport, Double min, Double max) {
            if (sport.getSlotTimings() == null || sport.getSlotTimings().isEmpty()) return false;

            // Calculate the lowest available price
            Double lowest = sport.getSlotTimings().stream()
                    .map(SlotTiming::getPrice)
                    .filter(Objects::nonNull)
                    .min(Double::compareTo)
                    .orElse(null);

            // If there are no valid prices, skip
            if (lowest == null) return false;

            // ✅ Now safely compare with nullable Double objects
            if (min != null && lowest < min) return false;
            if (max != null && lowest > max) return false;

            return true;
        }

        private SportResponseDTO toDTOWithMinPrice(Sports sport) {
            SportResponseDTO dto = toDTO(sport);
            double minPrice = sport.getSlotTimings() != null && !sport.getSlotTimings().isEmpty()
                    ? sport.getSlotTimings().stream().mapToDouble(SlotTiming::getPrice).min().orElse(0)
                    : 0;
            dto.setMinimumPrice(minPrice);
            return dto;
        }
        @Override
        public SportDetailsResponseDTO getSportDetailsById(Long sportId) {
            Sports sport = sportsRepository.findById(sportId)
                    .orElseThrow(() -> new EntityNotFoundException("Sport not found with ID: " + sportId));

            SportDetailsResponseDTO dto = new SportDetailsResponseDTO();
            dto.setId(sport.getId());
            dto.setName(sport.getName());
            dto.setDescription(sport.getDescription());
            dto.setCategoryName(sport.getCategory() != null ? sport.getCategory().getName() : null);
            dto.setLocationName(sport.getLocation() != null ? sport.getLocation().getName() : null);

            LocalDateTime now = LocalDateTime.now();
            DayOfWeek today = now.getDayOfWeek();
            LocalTime currentTime = now.toLocalTime();

            // ✅ Create list of next 7 days (rolling window starting from today)
            List<LocalDate> next7Days = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                next7Days.add(LocalDate.now().plusDays(i));
            }

            // ✅ Prepare weekly slots response
            List<SportDetailsResponseDTO.DayWiseSlotsDTO> weekSlots = new ArrayList<>();

            for (LocalDate date : next7Days) {
                DayOfWeek day = date.getDayOfWeek();

                List<SportDetailsResponseDTO.SlotTimingDTO> slotsForDay = sport.getSlotTimings().stream()
                        .filter(slot -> slot.getDayOfWeek() == day)
                        .filter(slot -> {
                            // For today → only show future slots
                            if (day == today) {
                                return slot.getStartTime().isAfter(currentTime);
                            }
                            return true;
                        })
                        .sorted(Comparator.comparing(SlotTiming::getStartTime))
                        .map(slot -> {
                            SportDetailsResponseDTO.SlotTimingDTO s = new SportDetailsResponseDTO.SlotTimingDTO();
                            s.setDayOfWeek(day.toString());
                            s.setStartTime(slot.getStartTime().toString());
                            s.setEndTime(slot.getEndTime().toString());
                            s.setPrice(slot.getPrice());
                            s.setSlotId(slot.getId());

                            // ✅ Added status check (BOOKED / TEMP_BLOCKED)
                            boolean isBlocked = bookingSlotRepository.existsBySlotTimingIdAndStatusIn(
                                    slot.getId(),
                                    List.of(SlotStatus.TEMP_BLOCKED, SlotStatus.BOOKED)
                            );
                            if (!isBlocked) {
                                s.setStatus("AVAILABLE");
                            } else {
                                s.setStatus("UNAVAILABLE");
                            }

                            return s;
                        })
                        .toList();

                SportDetailsResponseDTO.DayWiseSlotsDTO dayData = new SportDetailsResponseDTO.DayWiseSlotsDTO();
                dayData.setDay(day.toString());
                dayData.setDate(date.toString());
                dayData.setSlots(slotsForDay);

                weekSlots.add(dayData);
            }

            dto.setWeekSlots(weekSlots);

            // ✅ Convert media files (unchanged)
            List<SportDetailsResponseDTO.MediaFileDTO> mediaList = sport.getMediaFiles().stream().map(media -> {
                SportDetailsResponseDTO.MediaFileDTO m = new SportDetailsResponseDTO.MediaFileDTO();
                m.setFileType(media.getFileType());
                try {
                    String base64 = com.bookmyturf.utils.FileStorageUtil.convertFileToBase64(media.getPath());
                    m.setBase64Data(base64);
                } catch (Exception e) {
                    m.setBase64Data(null);
                }
                return m;
            }).toList();
            dto.setMediaFiles(mediaList);

            return dto;
        }

    }

