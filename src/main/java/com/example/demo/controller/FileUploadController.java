package com.example.demo.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;




@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads";
    private static final Logger logger = Logger.getLogger(FileUploadController.class.getName());

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        // Check if file is empty
        if (file.isEmpty()) {
            return new ResponseEntity<>("Please upload a file", HttpStatus.BAD_REQUEST);
        }

        try {
            // Create the uploads directory if it does not exist
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            // Save the file locally
            String filePath = UPLOAD_DIR + "/" + file.getOriginalFilename();
            File dest = new File(filePath);
            file.transferTo(dest);

            return new ResponseEntity<>("File uploaded successfully: " + filePath, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("File upload failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            // Build the file path
            Path filePath = Paths.get(UPLOAD_DIR).resolve(filename).normalize();

            Resource resource = new UrlResource(filePath.toUri());

            // Check if the file exists and is readable
            if (!resource.exists() || !resource.isReadable()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }


            // Return the file as a response
            return ResponseEntity.ok()
                    .body(resource);
        } catch (MalformedURLException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}