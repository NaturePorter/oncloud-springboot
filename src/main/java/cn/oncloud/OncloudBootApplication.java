package cn.oncloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 描述：项目启动类，程序入口
 * @author 余弘洋
 */
@SpringBootApplication
//扫描mp
@MapperScan("cn.oncloud.mapper")
public class OncloudBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(OncloudBootApplication.class, args);
        System.out.println("启动成功");
    }

}
