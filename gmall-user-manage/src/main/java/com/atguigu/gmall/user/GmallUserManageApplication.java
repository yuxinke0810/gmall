package com.atguigu.gmall.user;

import com.alibaba.dubbo.qos.common.Constants;
import com.alibaba.dubbo.qos.server.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "com.atguigu.gmall.user.mapper")
@ComponentScan(basePackages = "com.atguigu.gmall")
public class GmallUserManageApplication {

    public static void main(String[] args) {
        //配置dubbo.qos.port端口
        System.setProperty(Constants.QOS_PORT,"22224");
        //配置dubbo.qos.accept.foreign.ip是否关闭远程连接
        System.setProperty(Constants.ACCEPT_FOREIGN_IP,"false");
        SpringApplication.run(GmallUserManageApplication.class, args);
        //关闭QOS服务
        Server.getInstance().stop();
    }

}
