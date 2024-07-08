package com.spzx.product.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.redis.cache.RedisCache;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.product.api.domain.*;
import com.spzx.product.domain.SkuStock;
import com.spzx.product.mapper.ProductDetailsMapper;
import com.spzx.product.mapper.ProductMapper;
import com.spzx.product.mapper.ProductSkuMapper;
import com.spzx.product.mapper.SkuStockMapper;
import com.spzx.product.service.ProductService;
import com.spzx.product.service.ProductSkuService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 商品接口业务层处理
 *
 * @author spzx
 */
@Service
@Slf4j
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    @Resource
    private ProductMapper productMapper;

    @Resource
    private ProductSkuService productSkuService;

    @Resource
    private SkuStockMapper skuStockMapper;

    @Resource
    private ProductDetailsMapper productDetailsMapper;

    @Resource
    private ProductSkuMapper productSkuMapper;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 查询商品列表
     *
     * @param product 商品
     * @return 商品集合
     */
    @Override
    public List<Product> selectProductList(Product product) {
        return productMapper.selectProductList(product);
    }

    /**
     * 新增商品
     *
     * @param product 商品
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertProduct(Product product) {
        String username = SecurityUtils.getUsername();
        product.setCreateBy(username);
        productMapper.insert(product);
        List<ProductSku> productSkuList = product.getProductSkuList();
        for (int i = 0, size = productSkuList.size(); i < size; i++) {
            ProductSku productSku = productSkuList.get(i);
            productSku.setCreateBy(username);
            productSku.setSkuCode(product.getId() + "_" + i);
            productSku.setProductId(product.getId());
            productSku.setSkuName(product.getName() + " " + productSku.getSkuSpec());
            productSkuService.save(productSku);

            // 添加商品库存
            SkuStock skuStock = new SkuStock();
            skuStock.setCreateBy(username);
            skuStock.setSkuId(productSku.getId());
            skuStock.setTotalNum(productSku.getStockNum());
            skuStock.setLockNum(0);
            skuStock.setAvailableNum(productSku.getStockNum());
            skuStock.setSaleNum(0);
            skuStockMapper.insert(skuStock);
        }

        // 添加商品详情对象
        ProductDetails productDetails = new ProductDetails();
        productDetails.setCreateBy(username);
        productDetails.setProductId(product.getId());
        productDetails.setImageUrls(String.join(",", product.getDetailsImageUrlList()));
        productDetailsMapper.insert(productDetails);

        return product.getId().intValue();
    }

    /**
     * 根据主键ID查询商品
     *
     * @param id 商品主键ID
     * @return 商品
     */
    @Override
    public Product selectProductById(Long id) {
        // 1.根据商品ID获取商品信息
        Product product = productMapper.selectById(id);

        // 2.根据 商品ID 获取 商品SKU集合
        List<ProductSku> productSkuList = productSkuService.list(Wrappers.lambdaQuery(ProductSku.class)
                .eq(ProductSku::getProductId, id));
        // 3.商品SKU集合中收集商品SKU主键ID集合
        List<Long> productSkuIds = productSkuList.stream().map(ProductSku::getId).toList();
        // 4.根据商品SKU主键ID集合 查询 库存集合
        List<SkuStock> skuStockList = skuStockMapper.selectList(Wrappers.lambdaQuery(SkuStock.class)
                .in(SkuStock::getSkuId, productSkuIds)
                .select(SkuStock::getSkuId, SkuStock::getTotalNum));
        // 5.库存集合转为map，key为商品sku主键ID，value为总库存数totalNum
        Map<Long, Integer> skuIdToTotalNumMap = skuStockList.stream().collect(Collectors.toMap(SkuStock::getSkuId, SkuStock::getTotalNum));
        // 6.设置商品sku集合中每个商品sku的库存
        productSkuList.forEach(productSku -> productSku.setStockNum(skuIdToTotalNumMap.get(productSku.getId())));
        product.setProductSkuList(productSkuList);

        // 7.根据 商品主键ID 查询 商品详情信息
        ProductDetails productDetails = productDetailsMapper.selectOne(Wrappers.lambdaQuery(ProductDetails.class)
                .eq(ProductDetails::getProductId, id).select(ProductDetails::getImageUrls));
        product.setDetailsImageUrlList(Arrays.asList(productDetails.getImageUrls().split(",")));

        return product;
    }

    /**
     * 更新商品
     *
     * @param product 商品
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateProduct(Product product) {
        String username = SecurityUtils.getUsername();
        List<ProductSku> productSkuList = product.getProductSkuList();
        if (!CollectionUtils.isEmpty(productSkuList)) {
            productSkuList.forEach(productSku -> {
                // 更新sku库存信息
                SkuStock skuStock = skuStockMapper.selectOne(Wrappers.lambdaQuery(SkuStock.class)
                        .eq(SkuStock::getSkuId, productSku.getId()));
                skuStock.setTotalNum(productSku.getStockNum());
                skuStock.setAvailableNum(skuStock.getTotalNum() - skuStock.getLockNum());
                skuStock.setUpdateBy(username);
                skuStockMapper.updateById(skuStock);

                // 更新商品sku信息
                productSku.setUpdateBy(username);
                productSkuService.updateById(productSku);
            });
        }
        // 更新商品详情信息
        ProductDetails productDetails = productDetailsMapper.selectOne(Wrappers.lambdaQuery(ProductDetails.class)
                .eq(ProductDetails::getProductId, product.getId()));
        productDetails.setImageUrls(String.join(",", product.getDetailsImageUrlList()));
        productDetails.setUpdateBy(username);
        productDetailsMapper.updateById(productDetails);

        // 更新商品信息
        product.setUpdateBy(username);
        return productMapper.updateById(product);
    }

    /**
     * 批量删除商品
     *
     * @param ids 商品主键ID集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteProductsByIds(Long[] ids) {
        // 获取商品sku列表
        List<ProductSku> productSkuList = productSkuService.list(Wrappers.lambdaQuery(ProductSku.class)
                .in(ProductSku::getProductId, ids).select(ProductSku::getId));
        List<Long> skuIdList = productSkuList.stream().map(ProductSku::getId).toList();
        // 删除商品sku，条件：商品sku的商品id in (ids)
        productSkuService.remove(Wrappers.lambdaQuery(ProductSku.class)
                .in(ProductSku::getProductId, ids));
        // 删除sku库存，条件：sku库存的商品skuID in (skuIdList)
        skuStockMapper.delete(Wrappers.lambdaQuery(SkuStock.class)
                .in(SkuStock::getSkuId, skuIdList));
        // 删除商品详情表，条件：商品详情的商品id in (ids)
        productDetailsMapper.delete(Wrappers.lambdaQuery(ProductDetails.class)
                .in(ProductDetails::getProductId, ids));
        // 批量删除商品信息
        return productMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 商品审核
     *
     * @param id          商品主键ID
     * @param auditStatus 审核状态：0-初始值，1-通过，-1-未通过
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateAuditStatus(Long id, Integer auditStatus) {
        Product product = new Product();
        product.setId(id);
        if (auditStatus == 1) {
            product.setAuditStatus(1);
            product.setAuditMessage("审批通过");
        } else {
            product.setAuditStatus(-1);
            product.setAuditMessage("审批不通过");
        }
        productMapper.updateById(product);
    }

    /**
     * 更新上下架状态
     *
     * @param id     商品主键ID
     * @param status 线上状态：0-初始值，1-上架，-1-自主下架
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateStatus(Long id, Integer status) {
        Product product = new Product();
        product.setId(id);
        if (status == 1) {
            product.setStatus(1);
            // sku加入布隆过滤器
            RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter("sku:bloom:filter");
            List<ProductSku> productSkuList = productSkuMapper.selectList(Wrappers.lambdaQuery(ProductSku.class)
                    .eq(ProductSku::getProductId, id));
            productSkuList.forEach(item -> bloomFilter.add(item.getId()));
        } else {
            product.setStatus(-1);
        }
        productSkuService.update(Wrappers.lambdaUpdate(ProductSku.class)
                .eq(ProductSku::getProductId, id)
                .set(ProductSku::getStatus, product.getStatus())
                .set(ProductSku::getUpdateBy, SecurityUtils.getUsername()));
        productMapper.updateById(product);
    }

    @Override
    public List<ProductSku> getTopSale() {
        return productSkuMapper.getTopSale();
    }

    @Override
    public List<ProductSku> selectProductSkuList(SkuQuery skuQuery) {
        return productSkuMapper.selectProductSkuList(skuQuery);
    }

    @RedisCache(prefix = "product:")
    @Override
    public Product getProductById(Long productId) {
        return productMapper.selectById(productId);
    }

    @RedisCache(prefix = "product:details:")
    @Override
    public ProductDetails getProductDetailsByProductId(Long productId) {
        return productDetailsMapper.selectOne(Wrappers.lambdaQuery(ProductDetails.class)
                .eq(ProductDetails::getProductId, productId));
    }

    @RedisCache(prefix = "sku:stockVo:")
    @Override
    public SkuStockVo getSkuStockVoBySkuId(Long skuId) {
        SkuStock skuStock = skuStockMapper.selectOne(Wrappers.lambdaQuery(SkuStock.class)
                .eq(SkuStock::getSkuId, skuId)
                .select(SkuStock::getSkuId, SkuStock::getAvailableNum, SkuStock::getSaleNum));
        SkuStockVo skuStockVo = new SkuStockVo();
        BeanUtils.copyProperties(skuStock, skuStockVo);
        return skuStockVo;
    }

    @RedisCache(prefix = "sku:specValue:")
    @Override
    public Map<String, Long> getSkuSpecValueMapByProductId(Long productId) {
        List<ProductSku> productSkus = productSkuMapper.selectList(Wrappers.lambdaQuery(ProductSku.class)
                .eq(ProductSku::getProductId, productId)
                .select(ProductSku::getId, ProductSku::getSkuSpec));
        Map<String, Long> map = new HashMap<>();
        productSkus.forEach(productSku -> {
            map.put(productSku.getSkuSpec(), productSku.getId());
        });
        return map;
    }

    /**
     * 分布式锁查询SKU商品信息
     *
     * @param skuId
     * @return
     */
    @Override
    public ProductSku getProductSkuBySkuId(Long skuId) {
        try {
            // 1.优先从缓存中获取数据
            // 1.1 构建业务数据key 形式：前缀+业务唯一标识
            String dataKey = "product:sku:" + skuId;
            // 1.2 查询Redis获取业务数据
            ProductSku productSku = (ProductSku) redisTemplate.opsForValue().get(dataKey);
            // 1.3 命中缓存则直接返回
            if (productSku != null) {
                log.info("命中缓存，直接返回，KEY：{}，线程ID：{}，线程名称：{}", dataKey,
                        Thread.currentThread().getId(), Thread.currentThread().getName());
                return productSku;
            }

            // 2.尝试获取分布式锁(set k v ex nx 可能获取锁失败)
            // 2.1 构建锁key
            String lockKey = "product:sku:lock:" + skuId;
            // 2.2 使用uuid作为线程标识
            String lockValue = UUID.randomUUID().toString();
            // 2.3 使用Redis提供set nx ex 获取分布式锁
            Boolean flag = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, 5, TimeUnit.SECONDS);
            if (flag) {
                // 3 获取锁成功执行业务，将查询业务数据放入缓存Redis
                log.info("获取锁成功：{}，线程名称：{}", Thread.currentThread().getId(),
                        Thread.currentThread().getName());
                try {
                    productSku = this.getProductSkuBySkuIdFromDB(skuId);
                    long ttl = productSku == null ? 1 * 60 : 10 * 60;
                    redisTemplate.opsForValue().set(dataKey, productSku, ttl, TimeUnit.SECONDS);
                    return productSku;
                } finally {
                    // 4 业务执行完毕后 释放锁
                    String script =
                            "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                                    "then\n" +
                                    "   return redis.call(\"del\", KEYS[1])\n" +
                                    "else\n" +
                                    "   return 0\n" +
                                    "end";
                    DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                    redisScript.setScriptText(script);
                    redisScript.setResultType(Long.class);
                    redisTemplate.execute(redisScript, Arrays.asList(lockKey), lockValue);
                }
            } else {
                //获取锁失败则自旋
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.error("获取锁失败，自旋：{}，线程名称：{}", Thread.currentThread().getId(),
                        Thread.currentThread().getName());
                return this.getProductSkuBySkuId(skuId);
            }
        } catch (Exception e) {
            // 兜底解决方案：Redis服务有问题，将业务数据获取自动从数据库中获取
            log.error("[商品服务]查询商品信息异常：{}", e);
            return getProductSkuBySkuIdFromDB(skuId);
        }
    }

    private ProductSku getProductSkuBySkuIdFromDB(Long skuId) {
        return productSkuMapper.selectById(skuId);
    }

    @Override
    public List<SkuPrice> getSkuPriceListBySkuIds(List<Long> skuIds) {
        List<ProductSku> productSkuList = productSkuMapper.selectList(Wrappers.lambdaQuery(ProductSku.class)
                .in(ProductSku::getId, skuIds)
                .select(ProductSku::getSalePrice, ProductSku::getId, ProductSku::getMarketPrice));
        return productSkuList.stream().map(productSku -> {
            SkuPrice skuPrice = new SkuPrice();
            skuPrice.setSkuId(productSku.getId());
            skuPrice.setSalePrice(productSku.getSalePrice());
            skuPrice.setMarketPrice(productSku.getMarketPrice());
            return skuPrice;
        }).toList();
    }

    @Override
    public SkuPrice getSkuPriceBySkuId(Long skuId) {
        ProductSku productSku = productSkuMapper.selectOne(Wrappers.lambdaQuery(ProductSku.class)
                .eq(ProductSku::getId, skuId)
                .select(ProductSku::getSalePrice, ProductSku::getId, ProductSku::getMarketPrice));
        SkuPrice skuPrice = new SkuPrice();
        skuPrice.setSkuId(productSku.getId());
        skuPrice.setSalePrice(productSku.getSalePrice());
        skuPrice.setMarketPrice(productSku.getMarketPrice());
        return skuPrice;
    }
}
