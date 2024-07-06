package com.spzx.user.service;

import java.util.Map;

public interface SmsService {
    void send(String phone, String templateCode, Map<String, Object> params);
}
