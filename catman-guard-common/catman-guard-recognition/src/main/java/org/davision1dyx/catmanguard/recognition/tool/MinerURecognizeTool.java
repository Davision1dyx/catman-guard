package org.davision1dyx.catmanguard.recognition.tool;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.util.Timeout;
import org.davision1dyx.catmanguard.recognition.properties.RecognitionProperties;

import java.io.InputStream;

/**
 * @author Davison
 * @date 2026-05-02
 * @description
 */
@Slf4j
public class MinerURecognizeTool {

    private final RecognitionProperties recognitionProperties;

    public MinerURecognizeTool(RecognitionProperties recognitionProperties) {
        this.recognitionProperties = recognitionProperties;
    }

    /**
     * 调用文件解析接口
     * 使用 Apache HttpClient 5 替代 HttpURLConnection，提供更好的超时控制和连接管理
     *
     * @param fileName   文件名
     * @param fileStream 文件输入流
     * @return 解析结果
     */
    public String parseDocumentToMarkdown(String fileName, InputStream fileStream) {
        String url = recognitionProperties.getMineruEndpoint() + "/file_parse";
        int connectTimeout = recognitionProperties.getConnectTimeout();
        int responseTimeout = recognitionProperties.getResponseTimeout();

        // 配置请求超时
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(connectTimeout))
                .setResponseTimeout(Timeout.ofMilliseconds(responseTimeout))
                .build();

        try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build()) {

            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Accept", "application/json");

            // 构建 multipart 请求体
            HttpEntity multipartEntity = MultipartEntityBuilder.create()
                    .addBinaryBody("files", fileStream, ContentType.APPLICATION_OCTET_STREAM, fileName)
                    .addTextBody("backend", "pipeline")
                    .addTextBody("response_format_zip", "false")
                    .addTextBody("return_images", "false")
                    .addTextBody("return_model_output", "false")
                    .addTextBody("return_middle_json", "false").build();

            httpPost.setEntity(multipartEntity);

            log.info("开始调用文件解析接口: {}", url);

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getCode();
                log.info("文件解析接口响应状态码: {}", statusCode);

                HttpEntity responseEntity = response.getEntity();
                String responseBody = responseEntity != null ? EntityUtils.toString(responseEntity, "UTF-8") : "";

                if (statusCode == 200) {
                    log.info("文件解析接口调用成功，响应体长度: {}", responseBody.length());
                    return responseBody;
                } else {
                    log.error("文件解析接口调用失败，状态码: {}, 响应: {}", statusCode, responseBody);
                    throw new RuntimeException("文件解析接口调用失败: HTTP " + statusCode + ", " + responseBody);
                }
            }
        } catch (Exception e) {
            log.error("调用文件解析接口异常", e);
            throw new RuntimeException("调用文件解析接口失败: " + e.getMessage(), e);
        } finally {
            closeQuietly(fileStream);
        }
    }

    /**
     * 调用文件解析接口，获取 ZIP 格式响应
     * 使用 Apache HttpClient 5，支持流式下载大文件
     *
     * @param fileName   文件名
     * @param fileStream 文件输入流
     * @return ZIP 文件字节数组
     */
    public byte[] parseDocumentToZip(String fileName, InputStream fileStream) {
        String url = recognitionProperties.getMineruEndpoint() + "/file_parse";
        int connectTimeout = recognitionProperties.getConnectTimeout();
        int responseTimeout = recognitionProperties.getResponseTimeout();

        // 配置请求超时
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(connectTimeout))
                .setResponseTimeout(Timeout.ofMilliseconds(responseTimeout))
                .build();

        try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build()) {

            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Accept", "application/json");

            // 构建 multipart 请求体，启用 ZIP 格式和返回图片
            HttpEntity multipartEntity = MultipartEntityBuilder.create()
                    .addBinaryBody("files", fileStream, org.apache.hc.core5.http.ContentType.APPLICATION_OCTET_STREAM, fileName)
                    .addTextBody("backend", "pipeline").addTextBody("response_format_zip", "true")
                    .addTextBody("return_images", "true").addTextBody("return_model_output", "false")
                    .addTextBody("return_middle_json", "false").build();

            httpPost.setEntity(multipartEntity);

            log.info("开始调用文件解析接口（ZIP 模式）: {}", url);

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getCode();
                log.info("文件解析接口响应状态码: {}", statusCode);

                HttpEntity responseEntity = response.getEntity();
                if (statusCode == 200 && responseEntity != null) {
                    // 读取响应体为字节数组（ZIP 文件）
                    byte[] zipBytes = EntityUtils.toByteArray(responseEntity);
                    log.info("文件解析接口调用成功，ZIP 文件大小: {} bytes", zipBytes.length);
                    return zipBytes;
                } else {
                    String responseBody = responseEntity != null ? EntityUtils.toString(responseEntity, "UTF-8") : "";
                    log.error("文件解析接口调用失败，状态码: {}, 响应: {}", statusCode, responseBody);
                    throw new RuntimeException("文件解析接口调用失败: HTTP " + statusCode + ", " + responseBody);
                }
            }

        } catch (Exception e) {
            log.error("调用文件解析接口异常", e);
            throw new RuntimeException("调用文件解析接口失败: " + e.getMessage(), e);
        } finally {
            closeQuietly(fileStream);
        }
    }

    /**
     * 安静关闭输入流，忽略异常
     *
     * @param inputStream 输入流
     */
    private void closeQuietly(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (Exception ignored) {
                // 忽略关闭异常
            }
        }
    }
}
