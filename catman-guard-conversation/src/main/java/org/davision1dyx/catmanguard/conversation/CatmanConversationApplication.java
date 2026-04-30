package org.davision1dyx.catmanguard.conversation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Davison
 * @date 2026-04-30
 * @description 运维模块启动类
 */
@SpringBootApplication(scanBasePackages = "org.davision1dyx.catmanguard")
public class CatmanConversationApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatmanConversationApplication.class, args);
    }

}
