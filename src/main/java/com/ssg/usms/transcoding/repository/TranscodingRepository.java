package com.ssg.usms.transcoding.repository;

import java.io.File;

public interface TranscodingRepository {

    byte[] getOriginFile(String originFileUrl);

    void saveFiles(String path, File file);
}
