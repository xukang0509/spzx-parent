package com.spzx.common.redis.configure;

import com.spzx.common.core.utils.StringUtils;
import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * redisson配置信息
 */
@Data
@Configuration
@ConfigurationProperties("spring.data.redis")
public class RedissonConfig {
    private String host;
    private String password;
    private String port;

    private int timeout = 3000;
    private static String ADDRESS_PREFIX = "redis://";

    // 自动装配
    @Bean
    public RedissonClient redissonSingle() {
        Config config = new Config();
        if (!StringUtils.hasText(host)) {
            throw new RuntimeException("host is empty");
        }
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(ADDRESS_PREFIX + this.host + ":" + port)
                .setTimeout(timeout);
        if (StringUtils.hasText(password)) {
            serverConfig.setPassword(password);
        }
        return Redisson.create(config);
    }
}
