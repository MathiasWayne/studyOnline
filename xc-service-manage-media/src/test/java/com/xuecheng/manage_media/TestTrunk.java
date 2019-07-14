package com.xuecheng.manage_media;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author:zhangl
 * @date:2019/6/30
 * @description: 断点续传
 * 1、获取源文件长度
 * 2、根据设定的分块文件的大小计算出块数
 * 3、从源文件读数据依次向每一个块文件写数据。
 */
public class TestTrunk {

    @Test
    public void testTrunk() throws IOException {
        //获取源文件
        File sourceFile=new File("D:/studio/study.mp4");
        //生成trunk文件的路径
        String trunkPath="D:/studio/trunk/";
        File trunkFile=new File(trunkPath);

        //判断trunk文件是否存在 不存在创建
        if (!trunkFile.exists()) {
            trunkFile.mkdirs();
        }

        //计算分块大小
        long trunkSize=1024*1024*1;
        //分块数量
        long trunkCount = (long)Math.ceil(sourceFile.length() * 1.0 / trunkSize);

        if(trunkCount<0){
            trunkCount=1;
        }

        //io读写文件
        byte[] bytes=new byte[1024];


        RandomAccessFile randomAccessFile=new RandomAccessFile(sourceFile,"r");
        //根据分块文件的数量 对文件进行读写
        for (long i = 0; i < trunkCount; i++) {

            File file=new File(trunkPath+i);
            boolean newFile = file.createNewFile();
            if (newFile) {
                RandomAccessFile rw_file=new RandomAccessFile(file,"rw");
                //读写文件
                int len=-1;
                while((len=randomAccessFile.read(bytes))!=-1){
                  rw_file.write(bytes,0,len);
                  //如何写入的文件大小大于1024，将写入下一个文件
                  if(file.length()>=trunkSize){
                      break;
                  }
                }
                rw_file.close();
            }

        }
        randomAccessFile.close();
    }





    @Test
    public void testMerge() throws IOException{
        /**1、找到要合并的文件并按文件合并的先后进行排序。
        2、创建合并文件
        3、依次从合并的文件中读取数据向合并文件写入数*/

        //需要合并的目录
        String path="D:/studio/trunk/";

        //读取分块
        File file=new File(path);
        File mergeFile=new File("D:/studio/trunk/study.mp4");
        if(mergeFile.exists()){
            mergeFile.delete();
        }
        File[] files = file.listFiles();

        List<File> fileList= Arrays.asList(files);

        //对文件分片进行排序，否则在合并的时候无法生成源文件

        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
               if(Integer.parseInt(o1.getName())<Integer.parseInt(o2.getName())){
                   return -1;
               }
               return 1;
            }
        });

        //创建合并文件
        mergeFile.createNewFile();

        RandomAccessFile wr_file=new RandomAccessFile(mergeFile,"rw");
          wr_file.seek(0);
          byte[] b=new byte[1024];
          //读取分块文件 写入到合并文件
        for (File file1 : fileList) {
            RandomAccessFile r_file=new RandomAccessFile(file1,"rw");
           int len=-1;
           while((len=r_file.read(b))!=-1){
              wr_file.write(b,0,len);
           }
          r_file.close();
        }
       wr_file.close();
    }
}
