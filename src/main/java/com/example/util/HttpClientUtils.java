package com.example.util;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 *
 * 服务器端调用http请求工具类
 * @author GuoJun
 * @since  1.0.0
 * Created by GuoJun on 2018/05/16
 */
public final class HttpClientUtils {

    private static final Logger log = LoggerFactory.getLogger(HttpClientUtils.class);

    private static String SHARE_URL = "http://www.aigaogao.com/tools/history.html?s=";
    private HttpClientUtils() {
        throw new AssertionError("No com.example.util.HttpClientUtils instances for you!");
    }

    /**
     * 通过访问url方法写入本地文件
     *
     * @param url      资源地址
     * @param filePath 写入文件的地址
     */
    public static void urlWriteNative(String url, String filePath) {
        log.debug("=======http url : {} ===========filePath : {}", url, filePath);
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(url);

        // 建立的http连接，仍旧被response保持着，允许我们从网络socket中获取返回的数据
        // 为了释放资源，我们必须手动消耗掉response或者取消连接（使用CloseableHttpResponse类的close方法）
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            System.out.println(response.getStatusLine());

            HttpEntity entity = response.getEntity();

            // do something useful with the response body
            File file = new File(filePath);
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    boolean result = file.getParentFile().mkdirs();
                    if (!result) {
                        log.error("{} make dirs error!", file.getParentFile().getAbsoluteFile());
                        return;
                    }
                }
                boolean result = file.createNewFile();
                if (!result) {
                    log.error("{} createNewFile error!", filePath);
                }
            } else {
                log.info("==========>>>>>> file is exists!!! <<<<<<==========");
                log.info("\tfilePath: {}", filePath);
                log.info("===================================================");
                return;
            }
            entity.writeTo(new FileOutputStream(file));

            // and ensure it is fully consumed
            EntityUtils.consume(entity);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * post方法
     * @param url
     * @param parameterMap
     */
    public static void postMethod(String url, Map<String, String> parameterMap) {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(url);

        // 拼接参数
        List<NameValuePair> list = new ArrayList<>();

        if (MapUtils.isNotEmpty(parameterMap)) {
            for (Map.Entry<String, String> parameter : parameterMap.entrySet()) {
                list.add(new BasicNameValuePair(parameter.getKey(), parameter.getValue()));
            }
        }
        CloseableHttpResponse response = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(list));

            response = httpClient.execute(httpPost);

            System.out.println(response.getStatusLine());

            HttpEntity entity = response.getEntity();

            System.out.println(getString(entity));
            // do something useful with the response body

            // and ensure it is fully consumed

            // 消耗掉response
            EntityUtils.consume(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * post方法
     * @param url
     * @param parameter
     */
    public static String postMethod(String url, String parameter) {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(url);

        CloseableHttpResponse response = null;
        try {
            httpPost.setEntity(new StringEntity(parameter));

            response = httpClient.execute(httpPost);

            System.out.println(response.getStatusLine());

            HttpEntity entity = response.getEntity();

            String result = getString(entity);
            System.out.println(result);
            // do something useful with the response body

            // and ensure it is fully consumed

            // 消耗掉response
            EntityUtils.consume(entity);
            return result;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    /**
     *
     * @param code
     */
    public static String getShareInfo(String code) {
        if (StringUtils.isNotEmpty(code)) {
            CloseableHttpClient httpClient = HttpClients.createDefault();

            StringBuilder sb = new StringBuilder(SHARE_URL);
            sb.append(code);

            HttpGet httpGet = new HttpGet(sb.toString());
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                String content;
                if (HttpServletResponse.SC_OK == response.getStatusLine().getStatusCode()
                    && StringUtils.isNotEmpty(content = EntityUtils.toString(response.getEntity()))) {
                    EntityUtils.consume(response.getEntity());
                    return content;
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";

    }
    private static String getString(HttpEntity entity) {
        StringBuilder buffer = new StringBuilder();
        InputStream is;
        try {
            is = entity.getContent();
            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);

            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                buffer.append(inputLine);
            }
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    /**
     * 根据url下载文件保存到指定路径
     * @param urlStr
     * @param savePath
     */
    public static File downLoadFromUrl(String urlStr, String savePath) {
        //String urlStr = "http://ww1.sinaimg.cn/large/0065oQSqly1frsllc19gfj30k80tfah5.jpg";
        //String savePath = "E:\\javaReptile\\images\\";
        log.debug("img url : {}", urlStr);
        byte[] bArray = new byte[1024 * 8];
        InputStream inputStream = null;
        OutputStream out = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置超时间为10秒
            conn.setConnectTimeout(10 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            conn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
            conn.setRequestProperty("Charset", "UTF-8");

            //得到输入流
            inputStream = conn.getInputStream();
            StringBuilder fileName = new StringBuilder(savePath);
            fileName.append(UUID.randomUUID().toString().replace("-", "").toLowerCase())
                    .append(".png");
            File copy = new File(fileName.toString());
            if (!copy.exists()) {
                if (!copy.getParentFile().exists()) {
                    boolean succeed = copy.getParentFile().mkdirs();
                    if (!succeed) {
                        throw new IOException("创建[" + copy.getParentFile() + "]文件夹失败");
                    }
                }
                boolean succeed = copy.createNewFile();
                if (!succeed) {
                    throw new IOException("创建[" + fileName + "]文件失败");
                }
            }
            int i = 0;
            out = new FileOutputStream(copy);
            while (i != -1) {
                i = inputStream.read(bArray);
                if (i != -1) {
                    out.write(bArray, 0, i);
                }
            }
            return copy;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static String getPostfix(String url, String defaultFix) {
        String postfix;
        if (url != null && url.length() > 0) {
            int index = url.lastIndexOf(".");
            if (index > 0) {
                postfix = url.substring(url.lastIndexOf("."));
                if (postfix.indexOf("?") > 0) {
                    postfix = postfix.substring(0, postfix.indexOf("?"));
                }
                return postfix;
            }
        }
        return defaultFix;
    }

    public static void main(String[] args) {

        //String urlStr = "http://ww1.sinaimg.cn/large/0065oQSqly1frsllc19gfj30k80tfah5.jpg";
        String urlStr = "https://wx.qlogo.cn/mmopen/vi_32/AicWTxI9y8RccmeUxq3UricVQAtee0PGzDia1pgHakT3NhKXUthfXRClC5R2LUGz0EtnmIic8p5zej7CibbxQm3MSicQ/132";
        String savePath = "E:\\javaReptile\\images\\";
        downLoadFromUrl(urlStr, savePath);
    }
}
