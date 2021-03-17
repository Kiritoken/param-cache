package com.eli.param.cache.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * @author eli
 */
@Slf4j
public class HttpClientUtil {

    private static final PoolingHttpClientConnectionManager MANAGER;

    private static final CloseableHttpClient HTTP_CLIENT;

    private static final RequestConfig REQUEST_CONFIG;

    /**
     * 整个连接池的最大连接数
     */
    private static final int MAX_TOTAL = 50;

    /**
     * 每个route默认的最大连接数
     */
    private static final int DEFAULT_MAX_PER_ROUTE = 50;


    static {
        MANAGER = new PoolingHttpClientConnectionManager();
        MANAGER.setMaxTotal(MAX_TOTAL);
        MANAGER.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);

        REQUEST_CONFIG = RequestConfig.custom()
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();

        HTTP_CLIENT = HttpClients.custom().setConnectionManager(MANAGER)
                .setDefaultRequestConfig(REQUEST_CONFIG).build();
    }


    /**
     * get请求
     *
     * @param url 服务器接口
     * @return new String
     */
    public static String doGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        return getResponse(httpGet);
    }


    /**
     * post请求
     *
     * @param url    服务器接口
     * @param params 参数
     * @return new String
     * @throws UnsupportedEncodingException 编码异常
     */
    public static String doPost(String url, Map<String, Object> params) throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(url);
        HttpEntity httpEntity = new StringEntity(JSON.toJSONString(params));
        httpPost.setEntity(httpEntity);
        return getResponse(httpPost);
    }


    private static String getResponse(HttpRequestBase request) {
        CloseableHttpResponse response = null;
        try {
            response = HTTP_CLIENT.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (null == entity) {
                    throw new RuntimeException(String.format("请求%s返回结果为空", request.getURI().toString()));
                }
                return EntityUtils.toString(entity, "utf-8");
            }
            throw new RuntimeException(String.format("请求%s返回结果报文状态%s", request.getURI().toString(),
                    response.getStatusLine().getStatusCode()));
        } catch (Exception e) {
            log.error("请求{}异常", request.getURI().toString(), e);
            throw new RuntimeException(String.format("请求%s异常", request.getURI().toString()));
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("http资源释放异常", e);
                }
            }
        }
    }


}
