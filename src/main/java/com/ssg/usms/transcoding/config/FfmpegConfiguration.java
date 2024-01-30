package com.ssg.usms.transcoding.config;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Paths;

@Configuration
public class FfmpegConfiguration {

    @Value("${ffprobe.path}")
    private String ffprobePath;
    @Value("${ffmpeg.path}")
    private String ffmpegPath;

    @Bean
    public FFprobe fFprobe() throws IOException {

        return new FFprobe(Paths.get(ffprobePath).toString());
    }

    @Bean
    public FFmpeg fFmpeg() throws IOException {

        return new FFmpeg(Paths.get(ffmpegPath).toString());
    }

    @Bean
    public FFmpegExecutor executor() throws IOException {

        return new FFmpegExecutor(fFmpeg(), fFprobe());
    }
}
