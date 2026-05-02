package org.davision1dyx.catmanguard.recognition.config;

import org.davision1dyx.catmanguard.recognition.properties.RecognitionProperties;
import org.davision1dyx.catmanguard.recognition.tool.MinerURecognizeTool;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Davison
 * @date 2026-05-02
 * @description
 */
@Configuration
@EnableConfigurationProperties(RecognitionProperties.class)
public class RecognitionConfig {

    @Bean
    public MinerURecognizeTool minerURecognizeTool(RecognitionProperties recognitionProperties) {
        return new MinerURecognizeTool(recognitionProperties);
    }
}
