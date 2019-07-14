package com.xuecheng.manage_media.controller;

import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.service.MediaUploadService;
import com.xuecheng.media.MediaUploadControllerApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author:zhangl
 * @date:2019/7/1
 * @description:
 */

@RestController
@RequestMapping("/media/upload")
public class MediaUploadController implements MediaUploadControllerApi {
   @Autowired
   private MediaUploadService uploadService;
    @Override
    @PostMapping("/register")
    public ResponseResult register(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        return uploadService.register(fileMd5,fileName,fileSize,mimetype,fileExt);
    }

    @PostMapping("/checkchunk")
    @Override
    public CheckChunkResult checkchunk(String fileMd5, Integer chunk, Integer chunkSize) {
        return uploadService.checkchunk(fileMd5,chunk,chunkSize);

    }

    @Override
    @PostMapping("/uploadchunk")

    public ResponseResult uploadchunk(MultipartFile file, Integer chunk, String fileMd5)
    {
        return uploadService.uploadChunk(file,fileMd5,chunk);
    }

    @Override
    @PostMapping("/mergechunks")
    public ResponseResult mergechunks(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        return uploadService.megerChuncks(fileMd5,fileName,fileSize,mimetype,fileExt);
    }
}
