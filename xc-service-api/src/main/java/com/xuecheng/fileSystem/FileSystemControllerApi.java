package com.xuecheng.fileSystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import org.springframework.web.multipart.MultipartFile;

public interface FileSystemControllerApi {
    //图片上传
    UploadFileResult upload(MultipartFile multipartFile,
                            String fileTag,
                            String businessKey,
                            String metadata);
}
