package com.xuecheng.filesystem.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.filesystem.dao.FileSystemRepository;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.CustomException;
import com.xuecheng.framework.model.response.CommonCode;
import org.apache.commons.lang3.StringUtils;

import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author:zhangl
 * @date:2019/4/25
 * @description:
 */
@Service
public class FileSystemService {
    private static final Logger LOGGER=LoggerFactory.getLogger(FileSystem.class);
    @Value("${xuecheng.fastdfs.tracker_servers}")
    private String trackerServer;
    @Value("${xuecheng.fastdfs.connect_timeout_in_seconds}")
    private Integer connect_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.network_timeout_in_seconds}")
    private Integer network_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.charset}")
    private String charset;
    //文件上传
    @Autowired
    private FileSystemRepository fileSystemRepository;

    //初始化fdfs配置信息
    private void init_fdfs(){
        try {


          /*  ClientGlobal.initByTrackers(trackerServer);
            ClientGlobal.setG_charset(charset);
            ClientGlobal.setG_connect_timeout(connect_timeout_in_seconds);
            ClientGlobal.setG_network_timeout(network_timeout_in_seconds);*/
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
        } catch (Exception e) {
            LOGGER.error("上传文件上传异常"+e.getMessage(),e);
            throw new CustomException(FileSystemCode.FS_INITERROR);
        }

    }
   public UploadFileResult upload(MultipartFile file,String fileTag, String businessKey, String metadata){
       if (file==null) {
           throw new CustomException(FileSystemCode.FS_DELETEFILE_NOTEXISTS);
       }
       //文件上传到文件系统，返回图片在服务器的id
       String fileId = uploadStorage(file);
       //保存id到mongodb中
       FileSystem fileSystem=new FileSystem();
       fileSystem.setFileId(fileId);
       fileSystem.setFilePath(fileId);
       fileSystem.setBusinesskey(businessKey);
       fileSystem.setFiletag(fileTag);
       if (StringUtils.isNotEmpty(metadata)) {
           Map map = JSON.parseObject(metadata, Map.class);
           fileSystem.setMetadata(map);
       }
       fileSystem.setFileName(file.getOriginalFilename());
       fileSystem.setFileSize(file.getSize());
       fileSystem.setFileType(file.getContentType());
       fileSystemRepository.save(fileSystem);
       return new UploadFileResult(CommonCode.SUCCESS,fileSystem);
    }

    //保存图片到文件系统中
    private  String uploadStorage(MultipartFile file){

        try {
            init_fdfs();
            //获取trackerClient客户端
            TrackerClient trackerClient=new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageServer storeServer = trackerClient.getStoreStorage(trackerServer);
            StorageClient1 storageClient1=new StorageClient1(trackerServer,storeServer);
            byte[] bytes = file.getBytes();
            //获取文件的原始名称
            String fileName = file.getOriginalFilename();
            String ex = fileName.substring(fileName.lastIndexOf(".") + 1);
            String fileId = storageClient1.upload_file1(bytes, ex, null);
            return fileId;
        } catch (Exception e) {
            LOGGER.error("图片服务器获取连接失败"+e.getMessage(),e);
            throw new CustomException(FileSystemCode.FS_UPLOADFILE_SERVERFAIL);
        }
    }

 }
