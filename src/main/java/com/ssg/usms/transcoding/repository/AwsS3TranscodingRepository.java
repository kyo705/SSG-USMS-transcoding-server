package com.ssg.usms.transcoding.repository;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.TransferManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;

@Repository
@RequiredArgsConstructor
public class AwsS3TranscodingRepository implements TranscodingRepository {

    private final AmazonS3 amazonS3;
    private final TransferManager transferManager;

    @Value("${aws.s3.origin-video-bucket}")
    private String originVideoBucket;
    @Value("${aws.s3.transcode-video-bucket}")
    private String transcodingVideoBucket;


    @Override
    public byte[] getOriginFile(String originFileUrl) {

        try {
            return amazonS3.getObject(originVideoBucket, originFileUrl).getObjectContent().readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveFiles(String path, File directory) {

        transferManager.uploadDirectory(transcodingVideoBucket, path, directory, true);
    }
}
