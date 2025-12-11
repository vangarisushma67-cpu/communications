package com.lloyds.hackthon.customercoms.summary.service;

import com.google.cloud.storage.*;
import com.lloyds.hackthon.customercoms.summary.model.PensionData;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Logger;

public class GoogleCloudStorageService {
    private static final Logger logger = Logger.getLogger(GoogleCloudStorageService.class.getName());
    private final Storage storage;
    
    public GoogleCloudStorageService() {
        this.storage = StorageOptions.getDefaultInstance().getService();
    }
    
    /**
     * Downloads CSV file from Google Cloud Storage and parses it into PensionData objects
     */
    public List<PensionData> downloadAndParseCsv(String bucketName, String fileName) {
        try {
            logger.info("Downloading CSV file: " + fileName + " from bucket: " + bucketName);
            
            BlobId blobId = BlobId.of(bucketName, fileName);
            Blob blob = storage.get(blobId);
            
            if (blob == null) {
                throw new RuntimeException("File not found: " + fileName + " in bucket: " + bucketName);
            }
            
            byte[] content = blob.getContent();
            String csvContent = new String(content, StandardCharsets.UTF_8);
            
            logger.info("CSV file downloaded successfully. Size: " + content.length + " bytes");
            
            // Parse CSV content into PensionData objects
            List<PensionData> pensionDataList = new CsvToBeanBuilder<PensionData>(
                    new InputStreamReader(new ByteArrayInputStream(content), StandardCharsets.UTF_8))
                    .withType(PensionData.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();
            
            logger.info("Parsed " + pensionDataList.size() + " pension records from CSV");
            return pensionDataList;
            
        } catch (Exception e) {
            logger.severe("Error downloading or parsing CSV file: " + e.getMessage());
            throw new RuntimeException("Failed to download and parse CSV file", e);
        }
    }
    
    /**
     * Uploads PDF file to Google Cloud Storage
     */
    public void uploadPdf(String bucketName, String fileName, byte[] pdfContent) {
        try {
            logger.info("Uploading PDF file: " + fileName + " to bucket: " + bucketName);
            
            BlobId blobId = BlobId.of(bucketName, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType("application/pdf")
                    .build();
            
            storage.create(blobInfo, pdfContent);
            
            logger.info("PDF file uploaded successfully: " + fileName);
            
        } catch (Exception e) {
            logger.severe("Error uploading PDF file: " + e.getMessage());
            throw new RuntimeException("Failed to upload PDF file", e);
        }
    }
    
    /**
     * Checks if a file exists in the bucket
     */
    public boolean fileExists(String bucketName, String fileName) {
        try {
            BlobId blobId = BlobId.of(bucketName, fileName);
            Blob blob = storage.get(blobId);
            return blob != null && blob.exists();
        } catch (Exception e) {
            logger.warning("Error checking file existence: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Lists all files in a bucket with a specific prefix
     */
    public Iterable<Blob> listFiles(String bucketName, String prefix) {
        try {
            return storage.list(bucketName, Storage.BlobListOption.prefix(prefix)).iterateAll();
        } catch (Exception e) {
            logger.severe("Error listing files in bucket: " + e.getMessage());
            throw new RuntimeException("Failed to list files", e);
        }
    }
}