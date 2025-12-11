package com.lloyds.hackthon.customercoms.communication.util;

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
import java.util.ArrayList;
import java.util.List;

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
     * Lists all files in the pension-data-communication bucket.
     * @return List of file names in the bucket
     * @throws RuntimeException if there's an error accessing the bucket
     */
    public static List<String> listPensionDataFiles() {
        final String BUCKET_NAME = "pension-data-communication";
        List<String> fileNames = new ArrayList<>();
        
        try {
            Storage storage = StorageOptions.newBuilder()
                    .setProjectId(GCP_PROJECT_ID)
                    .setCredentials(GoogleCredentials.getApplicationDefault())
                    .build()
                    .getService();
            
            // List all objects in the bucket
            for (Blob blob : storage.list(BUCKET_NAME).iterateAll()) {
                fileNames.add(blob.getName());
            }
            
            return fileNames;
        } catch (Exception e) {
            throw new RuntimeException("Failed to list files in bucket " + BUCKET_NAME, e);
        }
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
