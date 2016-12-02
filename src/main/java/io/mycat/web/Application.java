package io.mycat.web;

import io.mycat.MycatStartup;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by 长宏 on 2016/11/25 0025.
 * 启动类
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
//        MycatStartup.startmain(args);//启动mycat服务器
        SpringApplication.run(Application.class, args);//启动tomcat，web服务器
    }
}
