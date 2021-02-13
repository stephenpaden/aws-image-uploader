package com.abbycodes.aws_image_upload.filestore;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@Service
public class FileStore {

    private final AmazonS3 s3;

    public FileStore(AmazonS3 s3) {
        this.s3 = s3;
    }

    public void save(
            String path,
            String fileName,
            Optional<Map<String, String>> optionalMetadata,
            InputStream inputStream) {

        ObjectMetadata metadata = new ObjectMetadata();
        optionalMetadata.ifPresent(map -> {
            if(!map.isEmpty()) {
                map.forEach(metadata::addUserMetadata);
            }
        });

            try {
                s3.putObject(path, fileName, inputStream, metadata);
            } catch(AmazonServiceException e) {
                throw new IllegalStateException("failed to store content to s3", e);
            }
    }

    public byte[] download(String path, String key) {
        try {
            S3Object s3object = s3.getObject(path, key);
            S3ObjectInputStream inputStream = s3object.getObjectContent();
            return IOUtils.toByteArray(inputStream);
        } catch (AmazonServiceException | IOException e) {
            throw new IllegalStateException("failed to download from s3", e);
        }
    }
}
