package com.atguigu.gmall.list;

import com.alibaba.dubbo.qos.common.Constants;
import com.alibaba.dubbo.qos.server.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.atguigu.gmall")
public class GmallListServiceApplication {

    public static void main(String[] args) {
        //配置dubbo.qos.port端口
        System.setProperty(Constants.QOS_PORT,"33333");
        //配置dubbo.qos.accept.foreign.ip是否关闭远程连接
        System.setProperty(Constants.ACCEPT_FOREIGN_IP,"false");
        SpringApplication.run(GmallListServiceApplication.class, args);
        //关闭QOS服务
        Server.getInstance().stop();
    }

}
