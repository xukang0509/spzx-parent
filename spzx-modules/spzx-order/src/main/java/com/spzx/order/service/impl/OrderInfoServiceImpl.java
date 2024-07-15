package com.spzx.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.cart.api.RemoteCartService;
import com.spzx.cart.api.domain.CartInfo;
import com.spzx.common.core.context.SecurityContextHolder;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.common.core.utils.StringUtils;
import com.spzx.common.rabbit.constant.MqConst;
import com.spzx.common.rabbit.service.RabbitService;
import com.spzx.order.api.domain.OrderInfo;
import com.spzx.order.api.domain.OrderItem;
import com.spzx.order.domain.OrderForm;
import com.spzx.order.domain.OrderLog;
import com.spzx.order.domain.TradeVo;
import com.spzx.order.mapper.OrderInfoMapper;
import com.spzx.order.mapper.OrderItemMapper;
import com.spzx.order.mapper.OrderLogMapper;
import com.spzx.order.service.OrderInfoService;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.api.domain.SkuLockVo;
import com.spzx.product.api.domain.SkuPrice;
import com.spzx.user.api.RemoteUserAddressService;
import com.spzx.user.api.domain.UserAddress;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 订单Service业务层处理
 *
 * @author xukang
 * @date 2024-07-03
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderItemMapper orderItemMapper;

    @Resource
    private RemoteCartService remoteCartService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RemoteProductService remoteProductService;

    @Resource
    private RemoteUserAddressService remoteUserAddressService;

    @Resource
    private OrderLogMapper orderLogMapper;

    @Resource
    private RabbitService rabbitService;

    /**
     * 查询订单列表
     *
     * @param orderInfo 订单
     * @return 订单
     */
    @Override
    public List<OrderInfo> selectOrderInfoList(OrderInfo orderInfo) {
        return orderInfoMapper.selectOrderInfoList(orderInfo);
    }

    @Override
    public OrderInfo selectOrderInfoById(Long id) {
        OrderInfo orderInfo = orderInfoMapper.selectById(id);
        List<OrderItem> orderItems = orderItemMapper.selectList(Wrappers.lambdaQuery(OrderItem.class)
                .eq(OrderItem::getOrderId, id));
        orderInfo.setOrderItemList(orderItems);
        return orderInfo;
    }

    @Override
    public TradeVo orderTradeData() {
        // 获取当前用户id
        Long userId = SecurityContextHolder.getUserId();
        R<List<CartInfo>> cartCheckedListRes = remoteCartService.getCartCheckedList(userId);
        if (cartCheckedListRes.getCode() == R.FAIL) {
            throw new ServiceException(cartCheckedListRes.getMsg());
        }
        List<CartInfo> cartInfoList = cartCheckedListRes.getData();
        if (CollectionUtils.isEmpty(cartInfoList)) {
            throw new ServiceException("购物车无选中商品");
        }
        // 将集合泛型从购物车改为订单明细
        List<OrderItem> orderItemList = cartInfoList.stream().map(cartInfo -> {
            OrderItem orderItem = new OrderItem();
            BeanUtils.copyProperties(cartInfo, orderItem);
            return orderItem;
        }).toList();
        // 订单总金额
        BigDecimal totalAmount = new BigDecimal(0);
        for (OrderItem orderItem : orderItemList) {
            totalAmount = totalAmount.add(orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuNum())));
        }
        //渲染订单确认页面-生成用户流水号
        String tradeNo = this.generateTradeNo(userId);
        stringRedisTemplate.opsForValue().set("spzx:order:trade:" + tradeNo, "1", 1, TimeUnit.HOURS);

        TradeVo tradeVo = new TradeVo();
        tradeVo.setTradeNo(tradeNo);
        tradeVo.setTotalAmount(totalAmount);
        tradeVo.setOrderItemList(orderItemList);
        return tradeVo;
    }

    @Override
    public TradeVo buyNow(Long skuId) {
        Long userId = SecurityContextHolder.getUserId();
        R<ProductSku> productSkuRes = remoteProductService.getProductSku(skuId);
        if (productSkuRes.getCode() == R.FAIL) {
            throw new ServiceException(productSkuRes.getMsg());
        }
        ProductSku productSku = productSkuRes.getData();
        OrderItem orderItem = new OrderItem();
        orderItem.setSkuId(skuId);
        orderItem.setSkuName(productSku.getSkuName());
        orderItem.setThumbImg(productSku.getThumbImg());
        orderItem.setSkuPrice(productSku.getSalePrice());
        orderItem.setSkuNum(1);

        BigDecimal totalAmount = productSku.getSalePrice();
        String tradeNo = generateTradeNo(userId);

        TradeVo tradeVo = new TradeVo();
        stringRedisTemplate.opsForValue().set("spzx:order:trade:" + tradeNo, "2", 1, TimeUnit.HOURS);

        tradeVo.setTradeNo(tradeNo);
        tradeVo.setTotalAmount(totalAmount);
        tradeVo.setOrderItemList(Arrays.asList(orderItem));

        return tradeVo;
    }

    @Override
    public IPage<OrderInfo> userOrderInfoList(Page<OrderInfo> pageParam, Integer orderStatus) {
        Long userId = SecurityContextHolder.getUserId();
        LambdaQueryWrapper<OrderInfo> queryWrapper = Wrappers.lambdaQuery(OrderInfo.class)
                .eq(userId != null, OrderInfo::getUserId, userId)
                .eq(orderStatus != null, OrderInfo::getOrderStatus, orderStatus)
                .orderByDesc(OrderInfo::getCreateTime);
        Page<OrderInfo> orderInfoPage = orderInfoMapper.selectPage(pageParam, queryWrapper);
        List<OrderInfo> orderInfoList = orderInfoPage.getRecords();
        if (!CollectionUtils.isEmpty(orderInfoList)) {
            List<Long> orderIdList = orderInfoList.stream().map(OrderInfo::getId).toList();
            List<OrderItem> orderItemList = orderItemMapper.selectList(Wrappers.lambdaQuery(OrderItem.class)
                    .in(OrderItem::getOrderId, orderIdList));
            Map<Long, List<OrderItem>> orderIdToOrderItemMap =
                    orderItemList.stream().collect(Collectors.groupingBy(OrderItem::getOrderId));
            orderInfoList.forEach(orderInfo -> {
                orderInfo.setOrderItemList(orderIdToOrderItemMap.get(orderInfo.getId()));
            });
        }
        return orderInfoPage;
    }

    // 系统取消订单
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void processCloseOrder(Long orderId) {
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        if (orderInfo != null && orderInfo.getOrderStatus() == 0) {
            orderInfo.setOrderStatus(-1);
            orderInfo.setCancelTime(new Date());
            orderInfo.setCancelReason("未支付自动取消");
            orderInfo.setUpdateTime(new Date());
            orderInfoMapper.updateById(orderInfo);

            // 记录日志
            OrderLog orderLog = new OrderLog();
            orderLog.setOrderId(orderInfo.getId());
            orderLog.setProcessStatus(-1);
            orderLog.setNote("系统取消订单");
            orderLog.setCreateTime(new Date());
            orderLogMapper.insert(orderLog);

            // 发送MQ消息通知商品系统解锁库存
            rabbitService.sendMessage(MqConst.EXCHANGE_PRODUCT, MqConst.ROUTING_UNLOCK, orderInfo.getOrderNo());
        }
    }

    // 用户手动取消订单
    @Override
    public void cancelOrder(Long orderId) {
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        if (null != orderInfo && orderInfo.getOrderStatus() == 0) {
            orderInfo.setOrderStatus(-1);
            orderInfo.setCancelTime(new Date());
            orderInfo.setCancelReason("用户取消订单");
            orderInfo.setUpdateTime(new Date());
            orderInfo.setUpdateBy(SecurityContextHolder.getUserName());
            orderInfoMapper.updateById(orderInfo);

            //记录日志
            OrderLog orderLog = new OrderLog();
            orderLog.setOrderId(orderInfo.getId());
            orderLog.setProcessStatus(-1);
            orderLog.setNote("用户取消订单");
            orderLog.setCreateBy(SecurityContextHolder.getUserName());
            orderLog.setCreateTime(new Date());
            orderLogMapper.insert(orderLog);
            //发送MQ消息通知商品系统解锁库存
            rabbitService.sendMessage(MqConst.EXCHANGE_PRODUCT, MqConst.ROUTING_UNLOCK, orderInfo.getOrderNo());
        }
    }

    // 根据订单号获取订单信息
    @Override
    public OrderInfo getByOrderNo(String orderNo) {
        OrderInfo orderInfo = orderInfoMapper.selectOne(Wrappers.lambdaQuery(OrderInfo.class)
                .eq(OrderInfo::getOrderNo, orderNo));
        List<OrderItem> orderItemList = orderItemMapper.selectList(Wrappers.lambdaQuery(OrderItem.class)
                .eq(OrderItem::getOrderId, orderInfo.getId()));
        orderInfo.setOrderItemList(orderItemList);
        return orderInfo;
    }

    @Override
    public void processPaySuccess(String orderNo) {
        // 获取订单信息
        OrderInfo orderInfo = orderInfoMapper.selectOne(Wrappers.lambdaQuery(OrderInfo.class)
                .eq(OrderInfo::getOrderNo, orderNo)
                .select(OrderInfo::getId, OrderInfo::getOrderStatus));
        // 未支付
        if (orderInfo.getOrderStatus() == 0) {
            orderInfo.setOrderStatus(1);
            orderInfo.setPaymentTime(new Date());
            orderInfoMapper.updateById(orderInfo);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long submitOrder(OrderForm orderForm) {
        // 获取当前登录用户id
        Long userId = SecurityContextHolder.getUserId();

        // 1.验证用户是否通过浏览器回退进行重复提交订单
        // 采用LUA脚本保证判断删除流水号原子性 KEYS[1]：流水号key；ARGV[1]：用户流水号
        String userTradeKey = "user:tradeNo:" + userId;
        String scriptText = """
                if redis.call("get", KEYS[1]) == ARGV[1]
                then
                    return redis.call("del", KEYS[1])
                else
                    return 0
                end""";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(scriptText, Long.class);
        Long flag = (Long) redisTemplate.execute(redisScript, Arrays.asList(userTradeKey), orderForm.getTradeNo());
        if (flag == 0) {
            throw new ServiceException("请勿重复提交订单，请尝试重试");
        }

        // 2.判断购物项
        List<OrderItem> orderItemList = orderForm.getOrderItemList();
        if (CollectionUtils.isEmpty(orderItemList)) {
            throw new ServiceException("请求不合法");
        }

        // 3.订单校验
        // 3.1 校验价格
        List<Long> skuIdList = orderItemList.stream().map(OrderItem::getSkuId).toList();
        R<List<SkuPrice>> skuPriceListRes = remoteProductService.getSkuPriceList(skuIdList);
        if (R.FAIL == skuPriceListRes.getCode()) {
            throw new ServiceException(skuPriceListRes.getMsg());
        }
        List<SkuPrice> skuPriceList = skuPriceListRes.getData();
        Map<Long, BigDecimal> skuIdToSalePriceMap =
                skuPriceList.stream().collect(Collectors.toMap(SkuPrice::getSkuId, SkuPrice::getSalePrice));
        String priceCheckResult = "";
        for (OrderItem orderItem : orderItemList) {
            if (orderItem.getSkuPrice().compareTo(skuIdToSalePriceMap.get(orderItem.getSkuId())) != 0) {
                priceCheckResult += orderItem.getSkuName() + "价格变化了；";
            }
        }
        if (StringUtils.isNotEmpty(priceCheckResult)) {
            // 更新购物车价格
            remoteCartService.updateCartPrice(userId);
            throw new ServiceException(priceCheckResult);
        }

        // 3.2 校验库存并锁定库存
        List<SkuLockVo> skuLockVoList = orderItemList.stream().map(orderItem -> {
            SkuLockVo skuLockVo = new SkuLockVo();
            skuLockVo.setSkuId(orderItem.getSkuId());
            skuLockVo.setSkuNum(orderItem.getSkuNum());
            return skuLockVo;
        }).toList();
        R<String> checkAndLockRes = remoteProductService.checkAndLock(orderForm.getTradeNo(), skuLockVoList);
        if (R.FAIL == checkAndLockRes.getCode()) {
            throw new ServiceException(checkAndLockRes.getMsg());
        }
        String checkAndLockStr = checkAndLockRes.getData();
        if (StringUtils.isNotEmpty(checkAndLockStr)) {
            throw new ServiceException(checkAndLockStr);
        }

        Long orderId = null;
        try {
            // 4.下单
            orderId = saveOrder(orderForm);
        } catch (Exception e) {
            e.printStackTrace();
            // 4.1 下单失败，解锁库存
            rabbitService.sendMessage(MqConst.EXCHANGE_PRODUCT, MqConst.ROUTING_UNLOCK, orderForm.getTradeNo());
            //抛出异常
            throw new ServiceException("下单失败");
        }
        // BUG: 立即购买时会删掉购物车中选中的商品
        // 解决：redis保存数据源
        String source = stringRedisTemplate.opsForValue().getAndDelete("spzx:order:trade:" + orderForm.getTradeNo());
        if (StringUtils.isNotEmpty(source) && Objects.equals(source, "1")) {
            // 删除购物车选项
            remoteCartService.deleteCartCheckedList(userId);
        }

        // 发送延迟消息，取消订单
        rabbitService.sendDelayMessage(
                MqConst.EXCHANGE_CANCEL_ORDER,
                MqConst.ROUTING_CANCEL_ORDER,
                String.valueOf(orderId),
                MqConst.CANCEL_ORDER_DELAY_TIME);

        return orderId;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long saveOrder(OrderForm orderForm) {
        Long userId = SecurityContextHolder.getUserId();
        String userName = SecurityContextHolder.getUserName();

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setUserId(userId);
        orderInfo.setNickName(userName);
        orderInfo.setOrderNo(orderForm.getTradeNo());
        orderInfo.setRemark(orderForm.getRemark());
        orderInfo.setOrderStatus(0);

        BigDecimal totalAmount = new BigDecimal(0);
        List<OrderItem> orderItemList = orderForm.getOrderItemList();
        for (OrderItem orderItem : orderItemList) {
            totalAmount = totalAmount.add(orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuNum())));
        }
        orderInfo.setTotalAmount(totalAmount);
        orderInfo.setCouponAmount(new BigDecimal(0));
        orderInfo.setOriginalTotalAmount(totalAmount.subtract(orderInfo.getCouponAmount()));
        orderInfo.setFeightFee(orderForm.getFeightFee());

        R<UserAddress> userAddressRes = remoteUserAddressService.getUserAddress(orderForm.getUserAddressId());
        if (userAddressRes.getCode() == R.FAIL) {
            throw new ServiceException(userAddressRes.getMsg());
        }
        UserAddress userAddress = userAddressRes.getData();
        orderInfo.setReceiverName(userAddress.getName());
        orderInfo.setReceiverPhone(userAddress.getPhone());
        orderInfo.setReceiverTagName(userAddress.getTagName());
        orderInfo.setReceiverProvince(userAddress.getProvinceCode());
        orderInfo.setReceiverCity(userAddress.getCityCode());
        orderInfo.setReceiverDistrict(userAddress.getDistrictCode());
        orderInfo.setReceiverAddress(userAddress.getFullAddress());
        orderInfoMapper.insert(orderInfo);

        // 保存订单明细
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderId(orderInfo.getId());
            orderItemMapper.insert(orderItem);
        }

        // 记录日志
        OrderLog orderLog = new OrderLog();
        orderLog.setOrderId(orderInfo.getId());
        orderLog.setProcessStatus(0);
        orderLog.setNote("提交订单");
        orderLog.setCreateTime(new Date());
        orderLogMapper.insert(orderLog);
        return orderInfo.getId();
    }

    /**
     * 渲染订单确认页面---生成用户流水号
     *
     * @param userId
     * @return
     */
    private String generateTradeNo(Long userId) {
        // 1、构建流水号Key
        String userTradeKey = "user:tradeNo:" + userId;
        // 2、构建流水号value
        String tradeNo = UUID.randomUUID().toString().replaceAll("-", "");
        // 3、将流水号存入Redis 暂存5分钟
        redisTemplate.opsForValue().set(userTradeKey, tradeNo, 5, TimeUnit.MINUTES);
        return tradeNo;
    }

    /**
     * 验证页面提交流水号是否有效
     *
     * @param userId
     * @param tradeNo
     * @return
     */
    private Boolean checkTradeNo(String userId, String tradeNo) {
        String userTradeKey = "user:tradeNo:" + userId;
        String cacheTradeNo = (String) redisTemplate.opsForValue().get(userTradeKey);
        return tradeNo.equals(cacheTradeNo);
    }

    /**
     * 删除流水号
     *
     * @param userId
     */
    private void deleteTradeNo(String userId) {
        String userTradeKey = "user:tradeNo:" + userId;
        redisTemplate.delete(userTradeKey);
    }
}
