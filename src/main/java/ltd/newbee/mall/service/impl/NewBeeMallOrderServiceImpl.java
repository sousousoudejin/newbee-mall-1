package ltd.newbee.mall.service.impl;

import cn.hutool.core.bean.BeanUtil;
import ltd.newbee.mall.common.*;
import ltd.newbee.mall.controller.vo.*;
import ltd.newbee.mall.dao.NewBeeMallGoodsMapper;
import ltd.newbee.mall.dao.NewBeeMallOrderItemMapper;
import ltd.newbee.mall.dao.NewBeeMallOrderMapper;
import ltd.newbee.mall.dao.NewBeeMallShoppingCartItemMapper;
import ltd.newbee.mall.entity.NewBeeMallGoods;
import ltd.newbee.mall.entity.NewBeeMallOrder;
import ltd.newbee.mall.entity.NewBeeMallOrderItem;
import ltd.newbee.mall.entity.StockNumDTO;
import ltd.newbee.mall.service.NewBeeMallOrderService;
import ltd.newbee.mall.util.NumberUtil;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class NewBeeMallOrderServiceImpl implements NewBeeMallOrderService {

    @Resource
    private NewBeeMallOrderMapper orderMapper;

    @Resource
    private NewBeeMallOrderItemMapper orderItemMapper;

    @Resource
    private NewBeeMallGoodsMapper goodsMapper;

    @Resource
    private NewBeeMallShoppingCartItemMapper cartItemMapper;


    @Override
    public NewBeeMallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId) {
        NewBeeMallOrder order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) NewBeeMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());

        if (!order.getUserId().equals(userId)) NewBeeMallException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());

        List<NewBeeMallOrderItem> itemList = orderItemMapper.selectByOrderId(order.getOrderId());

        if (CollectionUtils.isEmpty(itemList)) NewBeeMallException.fail(ServiceResultEnum.ORDER_ITEM_NOT_EXIST_ERROR.getResult());

        List<NewBeeMallOrderItemVO> itemVOS = BeanUtil.copyToList(itemList, NewBeeMallOrderItemVO.class);
        NewBeeMallOrderDetailVO detailVO = new NewBeeMallOrderDetailVO();
        BeanUtil.copyProperties(order, detailVO);
        detailVO.setOrderStatusString(NewBeeMallOrderStatusEnum.getNewBeeMallOrderStatusEnumByStatus(detailVO.getOrderStatus()).getName());
        detailVO.setPayStatusString(PayTypeEnum.getPayTypeEnumByType(detailVO.getPayType()).getName());
        detailVO.setNewBeeMallOrderItemVOS(itemVOS);
        return detailVO;

    }

    @Override
    public PageResult getMyOrders(PageQueryUtil pqu) {
        int total = orderMapper.getTotalNewBeeMallOrders(pqu);
        List<NewBeeMallOrder> orders = orderMapper.findNewBeeMallOrderList(pqu);
        List<NewBeeMallOrderListVO> orderListVOS = new ArrayList<>();

        if (total > 0) {
            orderListVOS = BeanUtil.copyToList(orders, NewBeeMallOrderListVO.class);

            for (NewBeeMallOrderListVO orderListVO : orderListVOS) {
                orderListVO.setOrderStatusString(NewBeeMallOrderStatusEnum.getNewBeeMallOrderStatusEnumByStatus(orderListVO.getOrderStatus()).getName());
            }

            List<Long> orderIds = orders.stream().map(NewBeeMallOrder::getOrderId).collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(orderIds)) {

                List<NewBeeMallOrderItem> items = orderItemMapper.selectByOrderIds(orderIds);
                Map<Long, List<NewBeeMallOrderItem>> itemMap = items.stream().collect(groupingBy(NewBeeMallOrderItem::getOrderId));
                for (NewBeeMallOrderListVO newBeeMallOrderListVO : orderListVOS) {
                    //封装每个订单列表对象的订单项数据
                    if (itemMap.containsKey(newBeeMallOrderListVO.getOrderId())) {
                        List<NewBeeMallOrderItem> orderItemListTemp = itemMap.get(newBeeMallOrderListVO.getOrderId());
                        //将NewBeeMallOrderItem对象列表转换成NewBeeMallOrderItemVO对象列表
                        List<NewBeeMallOrderItemVO> newBeeMallOrderItemVOS = BeanUtil.copyToList(orderItemListTemp, NewBeeMallOrderItemVO.class);
                        newBeeMallOrderListVO.setNewBeeMallOrderItemVOS(newBeeMallOrderItemVOS);
                    }
                }
            }
        }
        PageResult pageResult = new PageResult(orderListVOS, total, pqu.getLimit(), pqu.getPage());
        return pageResult;
    }

    @Override
    @Transactional
    public String saveOrder(NewBeeMallUserVO userVO, List<NewBeeMallShoppingCartItemVO> items) {
        List<Long> cartItemIds = items.stream()
                .map(NewBeeMallShoppingCartItemVO::getCartItemId)
                .collect(Collectors.toList());
        List<Long> goodsIds = items.stream()
                .map(NewBeeMallShoppingCartItemVO::getGoodsId)
                .collect(Collectors.toList());
        List<NewBeeMallGoods> mallGoods = goodsMapper.selectByPrimaryKeys(goodsIds);

        List<NewBeeMallGoods> statusResults = mallGoods.stream()
                .filter(goods -> goods.getGoodsSellStatus() != Constants.SELL_STATUS_UP)
                .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(statusResults))
            NewBeeMallException.fail(statusResults.get(0).getGoodsName() + ":已下架! 无法生成订单!");

        Map<Long, NewBeeMallGoods> goodsMap = mallGoods.stream().collect(Collectors.toMap(NewBeeMallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));

        for (NewBeeMallShoppingCartItemVO item : items) {
            if (!goodsMap.containsKey(item.getGoodsId()))
                NewBeeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            if (item.getGoodsCount() > goodsMap.get(item.getGoodsId()).getStockNum())
                NewBeeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
        }

        if (CollectionUtils.isEmpty(cartItemIds)
                && CollectionUtils.isEmpty(goodsIds)
                && CollectionUtils.isEmpty(mallGoods))
            NewBeeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());

        if (cartItemMapper.deleteBatch(cartItemIds) < 1)
            NewBeeMallException.fail(ServiceResultEnum.DB_ERROR.getResult());

        List<StockNumDTO> stockNumDTOS = BeanUtil.copyToList(items, StockNumDTO.class);
        int stockNum = goodsMapper.updateStockNum(stockNumDTOS);

        if (stockNum < 1)
            NewBeeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());

        //生成订单号
        String orderNo = NumberUtil.genOrderNo();
        int priceTotal = 0;

        NewBeeMallOrder order = new NewBeeMallOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userVO.getUserId());
        order.setUserAddress(userVO.getAddress());

        for (NewBeeMallShoppingCartItemVO item : items) {
            priceTotal += item.getGoodsCount() * item.getSellingPrice();
        }

        if (priceTotal < 1)
            NewBeeMallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());

        order.setTotalPrice(priceTotal);

        //订单body字段，用来作为生成支付单描述信息，暂时未接入第三方支付接口，故该字段暂时设为空字符串
        String extraInfo = "";
        order.setExtraInfo(extraInfo);

        if (orderMapper.insertSelective(order) < 1)
            NewBeeMallException.fail(ServiceResultEnum.DB_ERROR.getResult());

        List<NewBeeMallOrderItem> orderItems = new ArrayList<>();
        for (NewBeeMallShoppingCartItemVO item : items) {
            NewBeeMallOrderItem orderItem = new NewBeeMallOrderItem();
            BeanUtil.copyProperties(item,orderItem);
            orderItem.setOrderId(order.getOrderId());
            orderItems.add(orderItem);
        }

        if (orderItemMapper.insertBatch(orderItems) > 0)
            return orderNo;

        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }

    @Override
    public String cancelOrder(String orderNo, Long userId) {
        NewBeeMallOrder order = orderMapper.selectByOrderNo(orderNo);
        if (order == null)
            return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
        if (!userId.equals(order.getUserId()))
            NewBeeMallException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
        //订单状态判断
        if (order.getOrderStatus().intValue() == NewBeeMallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus()
                || order.getOrderStatus().intValue() == NewBeeMallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()
                || order.getOrderStatus().intValue() == NewBeeMallOrderStatusEnum.ORDER_CLOSED_BY_EXPIRED.getOrderStatus()
                || order.getOrderStatus().intValue() == NewBeeMallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) {
            return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
        }
        if (orderMapper.closeOrder(Collections.singletonList(order.getOrderId()),
                NewBeeMallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER) > 0
                && recoverStockNum(Collections.singletonList(order.getOrderId())))
            return ServiceResultEnum.SUCCESS.getResult();
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String finishOrder(String orderNo, Long userId) {
        NewBeeMallOrder order = orderMapper.selectByOrderNo(orderNo);
        if (order == null)
            return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
        //验证是否是当前userId下的订单，否则报错
        if (!userId.equals(order.getUserId()))
            return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();
        //订单状态判断 非出库状态下不进行修改操作
        if (order.getOrderStatus().intValue() != NewBeeMallOrderStatusEnum.ORDER_EXPRESS.getOrderStatus())
            return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();

        order.setOrderStatus((byte) NewBeeMallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
        order.setUpdateTime(new Date());
        if (orderMapper.updateByPrimaryKeySelective(order) > 0)
            return ServiceResultEnum.SUCCESS.getResult();

        return ServiceResultEnum.DB_ERROR.getResult();

    }

    @Override
    public NewBeeMallOrder getNewBeeMallOrderByOrderNo(String orderNo) {
        return orderMapper.selectByOrderNo(orderNo);
    }

    @Override
    public String paySuccess(String orderNo, int payType) {
        NewBeeMallOrder order = orderMapper.selectByOrderNo(orderNo);
        if (order == null)
            return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
        //订单状态判断 非待支付状态下不进行修改操作
        if (order.getOrderStatus().intValue() != NewBeeMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus())
            return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();

        order.setOrderStatus((byte) NewBeeMallOrderStatusEnum.ORDER_PAID.getOrderStatus());
        order.setPayType((byte) payType);
        order.setPayStatus((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus());
        order.setPayTime(new Date());
        order.setUpdateTime(new Date());
        if (orderMapper.updateByPrimaryKeySelective(order) > 0)
            return ServiceResultEnum.SUCCESS.getResult();
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    private boolean recoverStockNum(List<Long> orderIds) {

        //查询对应的订单项
        List<NewBeeMallOrderItem> newBeeMallOrderItems = orderItemMapper.selectByOrderIds(orderIds);
        //获取对应的商品id和商品数量并赋值到StockNumDTO对象中
        List<StockNumDTO> stockNumDTOS = BeanUtil.copyToList(newBeeMallOrderItems, StockNumDTO.class);
        //执行恢复库存的操作
        int updateStockNumResult = goodsMapper.recoverStockNum(stockNumDTOS);
        if (updateStockNumResult < 1) {
            NewBeeMallException.fail(ServiceResultEnum.CLOSE_ORDER_ERROR.getResult());
            return false;
        } else {
            return true;
        }

    }
}
