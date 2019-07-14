package com.xuecheng.manage_media.service;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.controller.MediaUploadController;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

/**
 * @author:zhangl
 * @date:2019/7/1
 * @description:
 */
@Service
public class MediaUploadService {
    private final static Logger LOGGER = LoggerFactory.getLogger(MediaUploadController.class);

    @Autowired
    MediaFileRepository mediaFileRepository;
    @Value("${xc-service-manage-media.upload-location}")
    String uploadPath;

    /**
     * 根据文件md5得到文件路径
     * 规则：
     * 一级目录：md5的第一个字符
     * 二级目录：md5的第二个字符
     * 三级目录：md5
     * 文件名：md5+文件扩展名
     *
     * @param fileMd5 文件md5值
     * @return 文件路径
     */

    //获取长传文件的路径
    private String getFileFolderPath(String fileMd5) {
        String path = uploadPath + fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/";
        return path;
    }

    //获取文件夹根目录
    private String getFilePath(String fileMd5, String fileExt) {
        String path = uploadPath + fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + fileMd5 + "." + fileExt;
        return path;
    }

    //获取上传文件的相对路径
    private String getFileFolderRelativePath(String fileMd5) {
        String path = fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/";
        return path;
    }

    //获取分块文件
    private String getFileChunkFolderPath(String fileMd5) {
        String path = fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/chunks" + "/";
        return path;
    }

    //创建文件上传的文件夹
    private boolean createFileFold(String fileMd5) {
        String fileFolderPath = getFileFolderPath(fileMd5);
        File file = new File(fileFolderPath);
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            return mkdirs;
        }
        return true;
    }

    //文件上传注册,判断文件是否存在，不存在则创建
    public ResponseResult register(String fileMd5, String fileName, Long fileSize, String
            mimetype, String fileExt) {
        String filePath = getFilePath(fileMd5, fileExt);

        File file = new File(filePath);

        //查询MongoDB中当前文件是否存在
        Optional<MediaFile> byId = mediaFileRepository.findById(fileMd5);
        if (file.exists() && byId.isPresent()) {
            //TODO  抛出异常当前上传的文件已经存在
        }

        //否则创建文件
        boolean fileFold = createFileFold(fileMd5);
        if (!fileFold) {
            //TODO 抛出异常创建文件夹异常
        }

        return new ResponseResult(CommonCode.SUCCESS);
    }

    //检查上传文件

    /**
     * @return
     * @Author zhangl
     * @Description //TODO
     * @Date 22:09 2019/7/2
     * @Param fileMd5加密文件名称，chunck分块下标，chunkSize分块大小
     **/

    public CheckChunkResult checkchunk(String fileMd5, Integer chunk, Integer chunkSize) {
        String chuncksPath = getFileChunkFolderPath(fileMd5);
        File file = new File(chuncksPath + chunk);
        if (file.exists()) {
            return new CheckChunkResult(MediaCode.CHUNK_FILE_EXIST_CHECK, true);
        } else {
            return new CheckChunkResult(MediaCode.CHUNK_FILE_EXIST_CHECK, false);
        }
    }

    //分块上传
    public ResponseResult uploadChunk(MultipartFile file, String fileMd5, Integer chunk) {
        if (file == null) {
            //TODO 上传文件为空，抛出异常
        }


        //检查保存分块文件的文件夹是否存在，不存在则创建
        boolean fileFold = createFileFold(fileMd5);
        File file1 = new File(getFileChunkFolderPath(fileMd5) + chunk);

        //上传文件

        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            inputStream = file.getInputStream();
            fileOutputStream = new FileOutputStream(file1);
            IOUtils.copy(inputStream, fileOutputStream);
        } catch (IOException e) {
            LOGGER.error("文件上传失败", e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                LOGGER.error("文件上传失败,关闭读取流失败", e);
            }
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                LOGGER.error("文件上传失败,关闭输出流失败", e);
            }
        }
        return new ResponseResult(CommonCode.SUCCESS);

    }

    //合并分块

    /**
     * @param  fileName, fileSize, mimetype, fileExt]
     * @return com.xuecheng.framework.model.response.ResponseResult
     * @Description 1）将块文件合并  2）校验文件md5是否正确 3）向Mongodb写入文件信息
     **/
    public ResponseResult megerChuncks(String fileMd5, String fileName, Long fileSize, String
            mimetype, String fileExt) {
        //获取存放文件的目录
        String fileChunkFolderPath = getFileChunkFolderPath(fileMd5);
        File file = new File(fileChunkFolderPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        //获取文件，如果文件存在则删除
        String filePath = getFilePath(fileMd5, fileExt);
        File mergeFile = new File(filePath);
        if (mergeFile.exists()) {
            //存在删除
            mergeFile.delete();
        }
        boolean newFile = false;

        try {
            mergeFile.createNewFile();
        } catch (IOException e) {
            LOGGER.error("创建文件失败", e);
        }
        if (!newFile) {
            //TODO 抛出异常 创建文件失败
        }

        //获取chunck文件夹下的所有文件
        List<File> allChunckFiles = getAllChunckFiles(file);
        //对排序后的文件分块进行合并
        File megerFiles = megerFile(mergeFile, allChunckFiles);

        boolean b = checkFileMD5(megerFiles, fileMd5);

        //如果文件不正确，抛出异常
        if (!b) {
            //TODO 抛出异常
        }

        //将文件信息保存到数据库
        MediaFile mediaFile = new MediaFile();
        mediaFile.setFileId(fileMd5);
        mediaFile.setFileName(fileMd5+"."+fileExt);
        mediaFile.setFileOriginalName(fileName);
//文件路径保存相对路径
        mediaFile.setFilePath(getFileFolderRelativePath(fileMd5));
        mediaFile.setFileSize(fileSize);
        mediaFile.setUploadTime(new Date());
        mediaFile.setMimeType(mimetype);
        mediaFile.setFileType(fileExt);
        //状态为上传成功
        mediaFile.setFileStatus("301002");
        MediaFile save = mediaFileRepository.save(mediaFile);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    private List<File> getAllChunckFiles(File file) {
        //获取分块文件下的所有文件
        File[] files = file.listFiles();
        //对文件进行排序
        List<File> files1 = Arrays.asList(files);
        Collections.sort(files1, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (Integer.parseInt(o1.getName()) > Integer.parseInt(o2.getName())) {
                    return 1;
                }
                return -1;
            }
        });
        return files1;
    }

    private File megerFile(File megerFile, List<File> chunckFile) {
        //megerFile 合并后的文件,chunckFile所有的分块
        //读取分块写入到megerFile
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(megerFile, "rw");
            byte[] b = new byte[1024];
            for (File file : chunckFile) {
                RandomAccessFile file1 = new RandomAccessFile(file, "rw");
                int len = -1;
                while ((len = file1.read(b)) != -1) {
                    randomAccessFile.write(b, 0, len);
                }
                file1.close();
            }
            randomAccessFile.close();
        } catch (IOException e) {
            LOGGER.error("合并文件失败", e);
        }
        return megerFile;
    }

    //校验MD5的值
    private boolean checkFileMD5(File megerFile, String fileMD5) {
        if (megerFile == null || StringUtils.isNotEmpty(fileMD5)) {
            return false;
        }
        FileInputStream inputStream = null;
        try {
            inputStream=new FileInputStream(megerFile);
             //获取到文件的md5
            String s = DigestUtils.md5Hex(inputStream);
            if(fileMD5.equalsIgnoreCase(s)){
                return true;
            }
        } catch (IOException e) {

            LOGGER.error("文件MD5比较失败",e);
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
               LOGGER.error("文件MD5比较输入流关闭失败",e);
            }
        }
        return false;
    }


}
