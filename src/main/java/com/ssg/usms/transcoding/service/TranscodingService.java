package com.ssg.usms.transcoding.service;

import com.ssg.usms.transcoding.repository.TranscodingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranscodingService {

    private final TranscodingRepository transcodingRepository;
    private final FFmpegExecutor executor;

    public void transcode(String originFileKey) {

        try {
            byte[] originData = transcodingRepository.getOriginFile(originFileKey);
            File originFile = Files.write(Paths.get(originFileKey), originData).toFile();


            // filename : streamKey(UUID 형태)-1641900000000.m3u8 or streamKey(UUID 형태)-1641900000000-001.ts
            String streamKey = originFileKey.substring(0, originFileKey.lastIndexOf("-"));
            long timestamp = Long.parseLong(originFileKey.split("[.]")[0].split("-")[5]);
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), TimeZone.getDefault().toZoneId());

            // 실제 다시보기 디렉토리 경로 : /streamKey/년/월/일
            String directory = Paths.get(
                    streamKey,
                    Integer.toString(dateTime.getYear()),
                    Integer.toString(dateTime.getMonth().getValue()),
                    Integer.toString(dateTime.getDayOfMonth())
            ).toString();

            Files.createDirectories(Paths.get(directory));

            String outputFilename = originFileKey.split("[.]")[0] + ".m3u8";

            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(originFile.getAbsolutePath())
                    .overrideOutputFiles(true)
                    .addOutput(Paths.get(directory, outputFilename).toString())
                    .setFormat("hls")
                    .setStartOffset(0, TimeUnit.MILLISECONDS) // Use null to start immediately
                    .addExtraArgs("-c:v", "libx264")
                    .addExtraArgs("-c:a", "aac")
                    .addExtraArgs("-b:v", "1024k")
                    .addExtraArgs("-b:a", "128k")
                    .addExtraArgs("-crf", "18")
                    .addExtraArgs("-tune", "zerolatency")
                    .addExtraArgs("-preset", "ultrafast")
                    .addExtraArgs("-hls_time", "20") // Set segment duration
                    .addExtraArgs("-hls_segment_type", "mpegts") // Set segment type
                    .addExtraArgs("-hls_list_size", "0") // Keep all segments in playlist
                    .addExtraArgs("-hls_segment_filename", Paths.get(directory,originFileKey.split("[.]")[0] + "-%03d.ts").toString()) // Segment filename pattern
                    .done();

            log.info("Transcoding is started now.");
            executor.createJob(builder).run();
            log.info("Transcoding is completed now.");

            File transcodedFileDirectory = Paths.get(directory).toFile();

            transcodingRepository.saveFiles(directory, transcodedFileDirectory);

            Thread.sleep(2000);

            FileUtils.delete(originFile);
            FileUtils.deleteDirectory(Paths.get(streamKey).toFile());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
