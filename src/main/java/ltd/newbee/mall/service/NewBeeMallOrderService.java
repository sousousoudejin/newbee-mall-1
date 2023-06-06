package ltd.newbee.mall.service;

import ltd.newbee.mall.controller.vo.NewBeeMallOrderDetailVO;
import ltd.newbee.mall.controller.vo.NewBeeMallShoppingCartItemVO;
import ltd.newbee.mall.controller.vo.NewBeeMallUserVO;
import ltd.newbee.mall.entity.NewBeeMallOrder;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;

import java.util.List;

public interface NewBeeMallOrderService {
    NewBeeMallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId);

    PageResult getMyOrders(PageQueryUtil pqu);

    String saveOrder(NewBeeMallUserVO userVO, List<NewBeeMallShoppingCartItemVO> items);

    String cancelOrder(String orderNo, Long userId);

    String finishOrder(String orderNo, Long userId);

    NewBeeMallOrder getNewBeeMallOrderByOrderNo(String orderNo);

    String paySuccess(String orderNo, int payType);
}
