package com.lloyds.hackthon.customercoms.rules.util;

import com.google.adk.tools.BaseTool;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.cloud.storage.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Common utilities for interacting with Google Cloud services.
 */
public class CommonUtils extends BaseTool {

    private static final String GCP_PROJECT_ID = "intelligentmachines";
    private static final String PATH_TO_JSON_KEY = "C:/Users/anant/AppData/Roaming/gcloud/application_default_credentials.json";

    public CommonUtils() {
        this("CommonTool", "Common tools for interacting with Google Cloud LLM Agents");
    }

    protected CommonUtils(@NotNull String name, @NotNull String description) {
        super(name, description);
    }

    public static String getGcpProjectId() {
        return GCP_PROJECT_ID;
    }

    public static String getSecret(String secretName) {
        try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
            SecretVersionName version = SecretVersionName.of(GCP_PROJECT_ID, secretName, "latest");
            return client.accessSecretVersion(version).getPayload().getData().toStringUtf8();
        } catch (IOException e) {
            throw new RuntimeException("Failed to access secret: " + secretName, e);
        }
    }

    public static String readCloudStorageFile(String bucketName, String objectName) {
        try {
            Storage storage = StorageOptions.newBuilder()
                    .setProjectId(GCP_PROJECT_ID)
                    .setCredentials(GoogleCredentials.getApplicationDefault())
                    .build().getService();
            BlobId blobId = BlobId.of(bucketName, objectName);
            byte[] content = storage.readAllBytes(blobId);
            return new String(content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Writes content to a file in Google Cloud Storage, creating the bucket if it doesn't exist.
     * @param bucketName The name of the bucket.
     * @param objectName The name of the object/file to write to.
     * @param content The string content to write.
     * @throws StorageException if there's an error accessing or creating the bucket.
     */
    public static void writeCloudStorageFile(String bucketName, String objectName, String content) {
        Storage storage = StorageOptions.newBuilder().setProjectId(GCP_PROJECT_ID).build().getService();
        
        try {
            // Try to get the bucket, will throw StorageException if it doesn't exist
            storage.get(bucketName);
        } catch (StorageException e) {
            // Bucket doesn't exist, create it
            storage.create(BucketInfo.of(bucketName));
        }
        
        // Now write the file
        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
        storage.create(blobInfo, content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Writes byte array content to a file in Google Cloud Storage.
     * @param bucketName The name of the bucket.
     * @param objectName The name of the object/file to write to.
     * @param content The byte array content to write.
     */
    /**
     * Writes byte array content to a file in Google Cloud Storage.
     * @param bucketName The name of the bucket.
     * @param objectName The name of the object/file to write to.
     * @param content The byte array content to write.
     * @param contentType The MIME type of the content.
     */
    public static void writeCloudStorageFile(String bucketName, String objectName, byte[] content, String contentType) {
        Storage storage = null;
        try {
            storage = StorageOptions.newBuilder().setProjectId(GCP_PROJECT_ID)
                    .setCredentials(GoogleCredentials.getApplicationDefault())
                    .build().getService();
            // Try to get the bucket, will throw StorageException if it doesn't exist
            storage.get(bucketName);
        } catch (StorageException | IOException e) {
            // Bucket doesn't exist, create it
            storage.create(BucketInfo.of(bucketName));
        }
        
        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
            .setContentType(contentType)
            .build();
        storage.create(blobInfo, content);
    }

    /**
     * Fetches content from a given URL using HTTP GET request.
     *
     * @param urlString The URL to fetch content from
     * @return The content as a String
     * @throws Exception if there's an error fetching the content
     */
    /**
     * Reads an Excel file from Cloud Storage and returns a map of sheet names to their content.
     * Each sheet's content is represented as a list of rows, where each row is a list of strings.
     */
    public static Map<String, List<List<String>>> readExcelFromCloudStorage(String bucketName, String objectName) throws IOException {
        Storage storage = StorageOptions.newBuilder()
                .setProjectId(GCP_PROJECT_ID)
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .build()
                .getService();

        BlobId blobId = BlobId.of(bucketName, objectName);
        byte[] content = storage.readAllBytes(blobId);

        Map<String, List<List<String>>> sheetsData = new HashMap<>();
        
        try (InputStream is = new ByteArrayInputStream(content);
             Workbook workbook = new XSSFWorkbook(is)) {
            
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = workbook.getSheetName(i);
                List<List<String>> sheetData = new ArrayList<>();
                
                for (Row row : sheet) {
                    List<String> rowData = new ArrayList<>();
                    for (Cell cell : row) {
                        switch (cell.getCellType()) {
                            case STRING:
                                rowData.add(cell.getStringCellValue());
                                break;
                            case NUMERIC:
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    rowData.add(cell.getDateCellValue().toString());
                                } else {
                                    rowData.add(String.valueOf(cell.getNumericCellValue()));
                                }
                                break;
                            case BOOLEAN:
                                rowData.add(String.valueOf(cell.getBooleanCellValue()));
                                break;
                            case FORMULA:
                                rowData.add(cell.getCellFormula());
                                break;
                            default:
                                rowData.add("");
                        }
                    }
                    sheetData.add(rowData);
                }
                sheetsData.put(sheetName, sheetData);
            }
        }
        
        return sheetsData;
    }

    public static String fetchFromUrl(String urlString) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        } else {
            throw new RuntimeException("Failed to fetch from URL: " + urlString + 
                                   ". Status code: " + response.statusCode());
        }
    }
}
