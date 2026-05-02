package org.davision1dyx.catmanguard.recognition.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Davison
 * @date 2026-05-02
 * @description
 */
@ConfigurationProperties(prefix = "catman.recognition")
public class RecognitionProperties {

    private String mineruEndpoint;
    private int connectTimeout = 30000;
    private int responseTimeout = 300000;

    public String getMineruEndpoint() {
        return mineruEndpoint;
    }

    public void setMineruEndpoint(String mineruEndpoint) {
        this.mineruEndpoint = mineruEndpoint;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getResponseTimeout() {
        return responseTimeout;
    }

    public void setResponseTimeout(int responseTimeout) {
        this.responseTimeout = responseTimeout;
    }
}
