package org.davision1dyx.catmanguard.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Davison
 * @date 2026-04-30
 * @description 管理模块启动类
 */
@SpringBootApplication(scanBasePackages = "org.davision1dyx.catmanguard")
public class CatmanAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatmanAdminApplication.class, args);
    }
}
