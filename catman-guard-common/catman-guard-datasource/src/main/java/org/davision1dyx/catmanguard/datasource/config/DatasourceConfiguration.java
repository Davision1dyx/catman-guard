package org.davision1dyx.catmanguard.datasource.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.davision1dyx.catmanguard.datasource.handle.MetaDataHandler;
import org.davision1dyx.catmanguard.datasource.properties.DatasourceProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author Davison
 * @date 2026-04-30
 * @description
 */
@Configuration
@EnableConfigurationProperties(value = DatasourceProperties.class)
public class DatasourceConfiguration {

    @Bean
    @ConditionalOnMissingBean(value = DataSource.class)
    public DataSource catmanDataSource(DatasourceProperties datasourceProperties) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(datasourceProperties.getUrl());
        config.setUsername(datasourceProperties.getUsername());
        config.setPassword(datasourceProperties.getPassword());
        
        if (datasourceProperties.getDriverClassName() != null) {
            config.setDriverClassName(datasourceProperties.getDriverClassName());
        }
        
        if (datasourceProperties.getMaximumPoolSize() != null) {
            config.setMaximumPoolSize(datasourceProperties.getMaximumPoolSize());
        }
        
        if (datasourceProperties.getMinimumIdle() != null) {
            config.setMinimumIdle(datasourceProperties.getMinimumIdle());
        }
        
        if (datasourceProperties.getConnectionTimeout() != null) {
            config.setConnectionTimeout(datasourceProperties.getConnectionTimeout());
        }
        
        if (datasourceProperties.getIdleTimeout() != null) {
            config.setIdleTimeout(datasourceProperties.getIdleTimeout());
        }
        
        if (datasourceProperties.getMaxLifetime() != null) {
            config.setMaxLifetime(datasourceProperties.getMaxLifetime());
        }
        
        if (datasourceProperties.getConnectionTestQuery() != null) {
            config.setConnectionTestQuery(datasourceProperties.getConnectionTestQuery());
        }
        
        if (datasourceProperties.getPoolName() != null) {
            config.setPoolName(datasourceProperties.getPoolName());
        }
        
        return new HikariDataSource(config);
    }

    @Bean
    public MetaDataHandler myMetaObjectHandler() {
        return new MetaDataHandler();
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        //防全表更新与删除插件
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        //分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
