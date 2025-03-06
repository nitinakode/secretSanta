package com.springbatch.batch.controller;

import com.springbatch.batch.service.RedisService;
import com.springbatch.batch.service.BatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    private RedisService redisService;
    @Autowired
    private BatchService batchService;

    //consider Your directory
    private static final String BASE_UPLOAD_DIR = "C:/Users/YOURDirectory/Downloads/batch-folder";

    @PostMapping("/employee")
    public ResponseEntity<String> uploadEmployeeFile(@RequestParam("file") MultipartFile file) {
        return uploadFile(file, "employee");
    }


    @PostMapping("/secretSanta")
    public ResponseEntity<String> uploadSecretSantaFile(@RequestParam("file") MultipartFile file) {
        return uploadFile(file, "secretSanta");
    }


    private ResponseEntity<String> uploadFile(MultipartFile file, String fileType) {
        if (file.isEmpty()) {
            logger.error("Uploaded file is empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty");
        }

        try {
            String subDir = fileType.equals("employee") ? "employee" : "secretSanta";
            Path uploadPath = getUploadPath(subDir);

            String fileName = file.getOriginalFilename();
            if (!validateFileName(fileName)) {
                logger.error("Invalid file name: {}", fileName);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file name");
            }

            String year = extractYear(fileName);
            String datePrefix = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String uniqueFileName = datePrefix+"_" +fileName;

            Path filePath = uploadPath.resolve(uniqueFileName);
            file.transferTo(filePath.toFile());


            batchService.setFileName(uniqueFileName);
            batchService.setYear(year);
            batchService.setFilePath(filePath.toString());

            logger.info("{} Batch Job started",fileType);
           if( batchService.runBatchJob(fileType.equals("secretSanta")))
           {
               return ResponseEntity.status(HttpStatus.OK).body(fileType + " job execution completed");
           }
           else
           {
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(fileType + " job execution Failed");

           }






        } catch (IOException e) {
            logger.error("Failed to save file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save file: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to process file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process file: " + e.getMessage());
        }
    }


    private Path getUploadPath(String subDir) throws IOException {
        Path uploadPath = Paths.get(BASE_UPLOAD_DIR, subDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        return uploadPath;
    }

    public static boolean validateFileName(String fileName) {
        return fileName.matches("^[A-Za-z]+_\\d{4}\\.csv$");
    }


    public static String extractYear(String fileName) {
        String[] parts = fileName.split("_");

        if (parts.length == 2) {
            return parts[1].replace(".csv", "");
        } else {
            throw new IllegalArgumentException("Invalid filename format. Filename must be in 'companyName_year.csv' format.");
        }
    }



    @GetMapping("/getAll")
    public ResponseEntity<?> getCache() {
        logger.info("Fetching secret santa cache");
        return new ResponseEntity(redisService.getValue("secret-santa"), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCache(@PathVariable("id") String id) {
        logger.info("Deleting secret santa cache for id: {}", id);
        redisService.deleteCache("secret-santa", id);
        return new ResponseEntity(redisService.getValue("secret-santa"), HttpStatus.OK);
    }
}
