package com.spzx.product;

import com.spzx.common.security.annotation.EnableCustomConfig;
import com.spzx.common.security.annotation.EnableRyFeignClients;
import com.spzx.product.mapper.ProductSkuMapper;
import jakarta.annotation.Resource;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

/**
 * 商品模块
 *
 * @author spzx
 */
@EnableCustomConfig
@EnableRyFeignClients
@SpringBootApplication
public class SpzxProductApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(SpzxProductApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  系统模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                " .-------.       ____     __        \n" +
                " |  _ _   \\      \\   \\   /  /    \n" +
                " | ( ' )  |       \\  _. /  '       \n" +
                " |(_ o _) /        _( )_ .'         \n" +
                " | (_,_).' __  ___(_ o _)'          \n" +
                " |  |\\ \\  |  ||   |(_,_)'         \n" +
                " |  | \\ `'   /|   `-'  /           \n" +
                " |  |  \\    /  \\      /           \n" +
                " ''-'   `'-'    `-..-'              ");
    }

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private ProductSkuMapper productSkuMapper;

    // springBoot 应用初始化后会执行一次该方法
    @Override
    public void run(String... args) throws Exception {
        // 初始化布隆过滤器
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter("sku:bloom:filter");
        // 设置数据规模，误判率，预计统计元素数量为100000，期望误差率为0.01
        bloomFilter.tryInit(100000, 0.01);
        // 项目初始化后将sku数据加入到布隆过滤器中
        List<Long> productSkuIds = productSkuMapper.getProductSkuIdsOnSale();
        productSkuIds.forEach(bloomFilter::add);
    }
}
