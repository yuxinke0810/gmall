package com.atguigu.gmall.cart;

import com.alibaba.dubbo.qos.common.Constants;
import com.alibaba.dubbo.qos.server.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.atguigu.gmall")
@MapperScan(basePackages = "com.atguigu.gmall.cart.mapper")
public class GmallCartServiceApplication {

    public static void main(String[] args) {
        //配置dubbo.qos.port端口
        System.setProperty(Constants.QOS_PORT,"22225");
        //配置dubbo.qos.accept.foreign.ip是否关闭远程连接
        System.setProperty(Constants.ACCEPT_FOREIGN_IP,"false");
        SpringApplication.run(GmallCartServiceApplication.class, args);
        //关闭QOS服务
        Server.getInstance().stop();
    }

}
