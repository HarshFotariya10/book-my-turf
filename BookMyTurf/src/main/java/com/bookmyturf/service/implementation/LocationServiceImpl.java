package com.bookmyturf.service.implementation;

import com.bookmyturf.entity.Category;
import com.bookmyturf.entity.Location;
import com.bookmyturf.entity.LocationMedia;
import com.bookmyturf.entity.User;
import com.bookmyturf.jparepository.CategoryRepository;
import com.bookmyturf.jparepository.LocationMediaRepository;
import com.bookmyturf.jparepository.LocationRepository;
import com.bookmyturf.jparepository.UserJpaRepository;
import com.bookmyturf.models.LocationDTO;
import com.bookmyturf.models.LocationMediaDTO;
import com.bookmyturf.security.JwtContext;
import com.bookmyturf.service.LocationService;
import com.bookmyturf.utils.FileStorageUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class LocationServiceImpl  implements LocationService {
    @Autowired
    private  LocationRepository locationRepo;
    @Autowired
    private  UserJpaRepository userRepo;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private LocationMediaRepository locationMediaRepository;

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPER_ADMIN')")
    public Location createLocation(LocationDTO dto) {
        Long userId = JwtContext.getUserId();
        if (userId == null) {
            throw new RuntimeException("Unauthorized - user not found in context");
        }
        User admin = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Admin user not found"));
        Location location = new Location();
        location.setName(dto.getName());
        location.setAddress(dto.getAddress());
        location.setCity(dto.getCity());
        location.setState(dto.getState());
        location.setPincode(dto.getPincode());
        location.setAdmin(admin);
        location.setCategories(new ArrayList<>());

        List<LocationMedia> mediaList = new ArrayList<>();
        if (dto.getMediaFiles() != null) {
            for (LocationMediaDTO mediaDTO : dto.getMediaFiles()) {
                LocationMedia media = new LocationMedia();
                media.setFileType(mediaDTO.getFileType());
                try {
                    String path = FileStorageUtil.saveBase64ToFile(mediaDTO.getBase64Data(), "uploads/location-media");
                    media.setPath(path);
                } catch (IOException e) {
                    throw new RuntimeException("Image saving failed", e);
                }
                media.setLocation(location);
                mediaList.add(media);
            }
        }

        location.setMediaFiles(mediaList);
        return locationRepo.save(location);
    }


    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPER_ADMIN')")
    public Location updateLocation(Long id, LocationDTO dto) {
        Location location = locationRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));

        location.setName(dto.getName());
        location.setAddress(dto.getAddress());
        location.setCity(dto.getCity());
        location.setState(dto.getState());
        location.setPincode(dto.getPincode());

        locationMediaRepository.deleteAll(location.getMediaFiles());
        location.getMediaFiles().clear();

        if (dto.getMediaFiles() != null) {
            for (LocationMediaDTO mediaDTO : dto.getMediaFiles()) {
                LocationMedia media = new LocationMedia();
                media.setFileType(mediaDTO.getFileType());
                try {
                    String path = FileStorageUtil.saveBase64ToFile(mediaDTO.getBase64Data(), "uploads/location-media");
                    media.setPath(path);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to save image during update", e);
                }
                media.setLocation(location);
                location.getMediaFiles().add(media);
            }
        }

        return locationRepo.save(location);
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPER_ADMIN')")
    public void deleteLocation(Long id) {
        locationRepo.deleteById(id);
    }

    @Override
    public LocationDTO getLocationById(Long id) {
        Location location = locationRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));

        LocationDTO dto = new LocationDTO();
        dto.setName(location.getName());
        dto.setAddress(location.getAddress());
        dto.setCity(location.getCity());
        dto.setState(location.getState());
        dto.setPincode(location.getPincode());

        List<LocationMediaDTO> mediaDTOList = new ArrayList<>();
        for (LocationMedia media : location.getMediaFiles()) {
            LocationMediaDTO mediaDTO = new LocationMediaDTO();
            mediaDTO.setFileType(media.getFileType());
            try {
                String base64 = FileStorageUtil.convertFileToBase64(media.getPath());
                mediaDTO.setBase64Data(base64);
            } catch (IOException e) {
                mediaDTO.setBase64Data(null); // or log error
            }
            mediaDTOList.add(mediaDTO);
        }

        dto.setMediaFiles(mediaDTOList);
        return dto;
    }


    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPER_ADMIN')")
    public List<Location> getAllLocationsByAdmin() {
        Long userId = JwtContext.getUserId();
        if (userId == null) {
            throw new RuntimeException("Unauthorized - user not found in context");
        }
        List<Location> locations = locationRepo.findByAdminId(userId);
        for (Location loc : locations) {
            for (LocationMedia media : loc.getMediaFiles()) {
                try {
                    String base64 = FileStorageUtil.convertFileToBase64(media.getPath());
                    media.setPath(base64); // optional if not needed
                } catch (IOException e) {
                    // Log or skip
                }
            }
        }
        return locations;
    }
    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPER_ADMIN')")
    public Category addCategoryToLocation(Long locationId, String categoryName) {
        Location location = locationRepo.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));

        Category category = new Category();
        category.setName(categoryName);
        category.setLocation(location);

        location.getCategories().add(category);

        return categoryRepository.save(category);
    }

}
