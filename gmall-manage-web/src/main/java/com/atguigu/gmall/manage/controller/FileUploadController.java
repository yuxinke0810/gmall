package com.atguigu.gmall.manage.controller;

import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin
public class FileUploadController {

    @Value("${fileServer.url}")
    private String fileUrl;

    /**
     * 上传文件
     * 获取上传文件，需要使用SpringMVC技术
     * @param file 文件
     * @return 图片路径地址
     */
    @PostMapping("/fileUpload")
    public String fileUpload(MultipartFile file){

        String imgUrl = fileUrl;

        //当文件不为空的时候，进行上传
        if (file != null) {
            try {
                String configFile = this.getClass().getResource("/tracker.conf").getFile();
                ClientGlobal.init(configFile);
                TrackerClient trackerClient=new TrackerClient();
                //获取连接
                TrackerServer trackerServer=trackerClient.getConnection();
                StorageClient storageClient=new StorageClient(trackerServer,null);
                //获取上传文件名
                String originalFilename = file.getOriginalFilename();
                //获取文件后缀名
                String extName = StringUtils.substringAfterLast(originalFilename, ".");
                //String orginalFilename="E://1.jpg";
                //获取本地文件
                //String[] upload_file = storageClient.upload_file(originalFilename, extName, null);
                //上传图片
                String[] upload_file = storageClient.upload_file(file.getBytes(), extName, null);
                for (int i = 0; i < upload_file.length; i++) {
                    String path = upload_file[i];
                    System.out.println("path = " + path);
                    imgUrl += "/" + path;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (MyException e) {
                e.printStackTrace();
            }
        }
        System.out.println("imgUrl = " + imgUrl);
        return imgUrl;
    }

}
