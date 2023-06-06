package ltd.newbee.mall.dao;

import ltd.newbee.mall.common.NewBeeMallOrderStatusEnum;
import ltd.newbee.mall.entity.NewBeeMallOrder;
import ltd.newbee.mall.util.PageQueryUtil;

import java.util.List;

public interface NewBeeMallOrderMapper {
    NewBeeMallOrder selectByOrderNo(String orderNo);

    int getTotalNewBeeMallOrders(PageQueryUtil pqu);

    List<NewBeeMallOrder> findNewBeeMallOrderList(PageQueryUtil pqu);

    int insertSelective(NewBeeMallOrder order);

    int closeOrder(List<Long> longs, NewBeeMallOrderStatusEnum newBeeMallOrderStatusEnum);

    int updateByPrimaryKeySelective(NewBeeMallOrder order);
}
