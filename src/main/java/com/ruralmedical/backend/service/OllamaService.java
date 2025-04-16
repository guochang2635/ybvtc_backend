package com.ruralmedical.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//


    @Service
    public class OllamaService {

        private static final Logger logger = LoggerFactory.getLogger(OllamaService.class);

        private final RestTemplate restTemplate;

        public OllamaService(RestTemplate restTemplate) {
            this.restTemplate = restTemplate;
            // 设置 RestTemplate 使用 UTF-8 编码
            restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
            // 初始化 WebClient
            WebClient webClient = WebClient.create("http://localhost:11434"); // 假设 Ollama 部署在本地 11434 端口
        }

        public String callOllama(String prompt) throws JsonProcessingException {

            String url = "http://localhost:11434/api/generate"; // 替换为实际的 Ollama API 地址
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAcceptCharset(List.of(StandardCharsets.UTF_8)); // 确保接受 UTF-8 编码

        // 构造请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "qwen2.5:7b");
        requestBody.put("prompt", prompt);
        requestBody.put("stream", true); // 使用布尔值 true

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setNodeFactory(com.fasterxml.jackson.databind.node.JsonNodeFactory.withExactBigDecimals(false));

        String jsonRequestBody;
        try {
            jsonRequestBody = objectMapper.writeValueAsString(requestBody);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert request body to JSON", e);
        }
        HttpEntity<String> request = new HttpEntity<>(jsonRequestBody, headers);
        // 发送 POST 请求
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        // 获取响应体
        String responseBody = response.getBody();
        String fixedJsonData = "[" + responseBody.trim().replaceAll("\\}\\s*\\{", "},{") + "]";
        // 处理 JSON 数据
        JSONArray jsonArray = new JSONArray(fixedJsonData);

        StringBuilder fullResponse = new StringBuilder();
        // 解析 JSON 响应
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String result = jsonObject.getString("response");
            fullResponse.append(result);
        }
        // 输出完整的句子
        return fullResponse.toString();
            // ... 现有的 callOllama 方法实现 ...

        }

    }
