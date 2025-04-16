package com.ruralmedical.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ruralmedical.backend.pojo.ResponseMessage;
import com.ruralmedical.backend.service.OllamaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AiController {

    private static final Logger log = LoggerFactory.getLogger(AiController.class);
    @Autowired
    private OllamaService ollamaService;
    @PostMapping("/ai")
    public ResponseMessage<String> generateResponse(@RequestBody String prompt) throws JsonProcessingException {
        String response = ollamaService.callOllama(prompt);
        log.info("返回成功");
        return ResponseMessage.success(response);
    }



}
