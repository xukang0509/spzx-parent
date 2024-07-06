package com.spzx.user.service.impl;

import com.spzx.user.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    // TODO: 发送短信验证码服务方法
    @Override
    public void send(String phone, String templateCode, Map<String, Object> params) {

    }
}
