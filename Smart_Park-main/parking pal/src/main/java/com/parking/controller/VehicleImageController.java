package com.parking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/parking/images")
public class VehicleImageController {
    @Autowired
    private VehicleImageService imageService;

    @PostMapping("/upload/{ticketId}")
    public ResponseEntity<VehicleImageDTO> uploadImage(
            @PathVariable String ticketId,
            @RequestParam("image") MultipartFile file) {
        
        if (file.isEmpty()) {
            throw new InvalidRequestException("Please select an image file to upload");
        }

        String fileName = imageService.saveVehicleImage(file, ticketId);
        return ResponseEntity.ok(VehicleImageDTO.builder()
            .url("/api/parking/images/" + fileName)
            .build());
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(imagePath).resolve(fileName);
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
            } else {
                throw new ResourceNotFoundException("Image not found: " + fileName);
            }
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Image not found: " + fileName);
        }
    }
} 