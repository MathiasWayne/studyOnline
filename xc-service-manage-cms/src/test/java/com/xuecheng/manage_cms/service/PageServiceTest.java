package com.xuecheng.manage_cms.service;




import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PageServiceTest {
  @Autowired
  private PageService service;
  @Autowired
  private GridFsTemplate gridFsTemplate;
    @Test
    public void getHtml() {
        String html = service.getHtml("5aed94530e66185b64804c11");
        System.out.println(html);
    }

    @Test
    public void storeTemplate(){

        try {
            File file = new File("D:\\course.ftl");
            FileInputStream inputStream = new FileInputStream(file);
            //保存模版文件内容
            ObjectId id = gridFsTemplate.store(inputStream, "课程详情模板文件", "");
            System.out.println(id);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}