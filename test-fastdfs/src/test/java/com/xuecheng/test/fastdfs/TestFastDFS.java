package com.xuecheng.test.fastdfs;

import org.csource.fastdfs.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;


/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFastDFS {

    @Test
    public void upload() {
        try {
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer server = trackerClient.getConnection();
            StorageServer storeStorage = trackerClient.getStoreStorage(server);
            StorageClient1 storageClient1 = new StorageClient1(server, storeStorage);
            //文件路径
            String file_string = "D:/timg.jpg";
            String id = storageClient1.upload_file1(file_string, "jpg", null);
            System.out.println(id);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void download() {
        try {
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer server = trackerClient.getConnection();
            StorageServer storeStorage = trackerClient.getStoreStorage(server);
            StorageClient1 storageClient1 = new StorageClient1(server, storeStorage);
            //文件路径
            String file_string = "group1/M00/00/00/rBOIn1zAgm-Aa_8GAAJsON7tmSc409.jpg";
            byte[] bytes = storageClient1.download_file1(file_string);
            FileOutputStream outputStream=new FileOutputStream(new File("e:/time.jpg"));
            outputStream.write(bytes);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {

        }
    }

}
