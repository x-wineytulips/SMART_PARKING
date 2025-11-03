package com.parking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class VehicleImageService {
    @Autowired
    private ImageRepository imageRepository;
    
    @Value("${parking.images.path}")
    private String imagePath;
    
    public String saveVehicleImage(MultipartFile file, String ticketId) {
        try {
            // Generate unique filename
            String fileName = ticketId + "_" + System.currentTimeMillis() + 
                            getFileExtension(file.getOriginalFilename());
            
            // Create directory if it doesn't exist
            Path directory = Paths.get(imagePath);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            
            // Save image file
            Path filePath = directory.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Save image metadata
            VehicleImage image = VehicleImage.builder()
                .ticketId(ticketId)
                .fileName(fileName)
                .fileType(file.getContentType())
                .uploadTime(LocalDateTime.now())
                .imageType(ImageType.ENTRY)
                .build();
            
            imageRepository.save(image);
            
            return fileName;
        } catch (IOException e) {
            log.error("Failed to save vehicle image", e);
            throw new ImageProcessingException("Could not save image file: " + e.getMessage());
        }
    }

    public List<VehicleImageDTO> getVehicleImages(String ticketId) {
        return imageRepository.findByTicketId(ticketId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private VehicleImageDTO convertToDTO(VehicleImage image) {
        return VehicleImageDTO.builder()
            .id(image.getId())
            .url("/api/parking/images/" + image.getFileName())
            .uploadTime(image.getUploadTime())
            .imageType(image.getImageType())
            .build();
    }
} 