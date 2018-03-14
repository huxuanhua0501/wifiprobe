package com.wifiprobe.controller;

import com.alibaba.fastjson.JSON;
import com.wifiprobe.utils.PropertiesUtil;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class WifiProbeController
{
    private Logger logger = LoggerFactory.getLogger(WifiProbeController.class);

    @Autowired
    PropertiesUtil propertiesUtil;

    @Autowired
    private Environment env;

    @PostMapping({"probe"})
    public String probe(@RequestParam("file") MultipartFile file) { if (!file.isEmpty()) {
        String remote_url = this.env.getProperty("url");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            String fileName = file.getOriginalFilename();
            HttpPost httpPost = new HttpPost(remote_url);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", file.getInputStream(), ContentType.MULTIPART_FORM_DATA, fileName);
            builder.addTextBody("filename", fileName);
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null)
            {
                EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        String path = this.env.getProperty("wifiprobe.path") + File.separator + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + File.separator + new SimpleDateFormat("HH").format(new Date());
        File fileurl = new File(path);
        if (!fileurl.exists())
            fileurl.mkdirs();
        try
        {
            this.logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + ";fileName===" + file.getOriginalFilename() + ";fileSize==" + file.getSize());
            file.transferTo(new File(path + File.separator + file.getOriginalFilename()));
            Map ret = new HashMap();
            ret.put("status", Integer.valueOf(0));
            ret.put("msg", "File upload successfully");
            ret.put("version", "");
            return JSON.toJSONString(ret);
        }
        catch (Exception e) {
            this.logger.debug("异常：" + e);

            Map err = new HashMap();
            err.put("status", "1001");
            err.put("msg", "后台异常");
            err.put("version", "");
            return JSON.toJSONString(err);
        }
    }
        this.logger.debug("异常：没收到文件");
        Map err = new HashMap();
        err.put("status", "1002");
        err.put("msg", "没有收到文件");
        err.put("version", "");
        return JSON.toJSONString(err); }

    @GetMapping({"go"})
    public void httPost() throws IOException
    {
        String status = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try
        {
            HttpPost httppost = new HttpPost("http://localhost:8044//probe");

            File file = new File("E:\\iV6zPDpr4r29LfnE3wT8DA.._WiFiProbe_1494471456.log");
            FormBodyPart filebody = new FormBodyPart("file", new FileBody(file, ContentType.MULTIPART_FORM_DATA, file.getName()));

            TreeMap queryParmates = new TreeMap();
            queryParmates.put("appkey", "12sssss");
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();

            entityBuilder = entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            System.err.println(filebody.getName());
            entityBuilder = entityBuilder.addPart(filebody.getName(), filebody.getBody());

            Iterator it = queryParmates.keySet().iterator();
            String key = null;
            while (it.hasNext()) {
                key = (String)it.next();
                entityBuilder.addTextBody(key, (String)queryParmates.get(key), ContentType.TEXT_PLAIN.withCharset("UTF-8"));
            }

            httppost.setEntity(entityBuilder.build());

            httpclient.execute(httppost);
        }
        finally
        {
            httpclient.close();
        }
    }

    public static void main(String[] args) {
        System.err.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }
}