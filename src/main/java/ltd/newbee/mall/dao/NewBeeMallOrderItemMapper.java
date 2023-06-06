package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.NewBeeMallOrderItem;

import java.util.List;

public interface NewBeeMallOrderItemMapper {
    List<NewBeeMallOrderItem> selectByOrderId(Long orderId);

    List<NewBeeMallOrderItem> selectByOrderIds(List<Long> orderIds);

    int insertBatch(List<NewBeeMallOrderItem> orderItems);
}
