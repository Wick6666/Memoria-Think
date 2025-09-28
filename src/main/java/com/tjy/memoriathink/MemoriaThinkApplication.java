package com.tjy.memoriathink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MemoriaThinkApplication {

/**
 * Spring Boot应用程序的主入口方法
 * 通过SpringApplication的静态run方法启动应用程序
 *
 * @param args 命令行参数，可用于配置应用程序的启动参数
 */
    public static void main(String[] args) { // 程序入口点，main方法
        SpringApplication.run(MemoriaThinkApplication.class, args); // 调用SpringApplication的run方法启动Spring Boot应用
    }

}
