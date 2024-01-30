package com.ssg.usms.transcoding.controller;

import com.ssg.usms.transcoding.dto.HttpRequestTranscodingDto;
import com.ssg.usms.transcoding.service.TranscodingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TranscodingController {

    private final TranscodingService transcodingService;

    @PostMapping("/transcoding")
    public void transcode(@RequestBody HttpRequestTranscodingDto requestBody) {

        transcodingService.transcode(requestBody.getOriginFileKey());
    }
}
