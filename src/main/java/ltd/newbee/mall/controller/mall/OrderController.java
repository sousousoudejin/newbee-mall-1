package ltd.newbee.mall.controller.mall;

import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.NewBeeMallException;
import ltd.newbee.mall.common.NewBeeMallOrderStatusEnum;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.controller.vo.NewBeeMallOrderDetailVO;
import ltd.newbee.mall.controller.vo.NewBeeMallShoppingCartItemVO;
import ltd.newbee.mall.controller.vo.NewBeeMallUserVO;
import ltd.newbee.mall.entity.NewBeeMallOrder;
import ltd.newbee.mall.service.NewBeeMallOrderService;
import ltd.newbee.mall.service.NewBeeMallShoppingCartService;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    @Autowired
    private NewBeeMallShoppingCartService cartService;

    @Resource
    private NewBeeMallOrderService orderService;


    @GetMapping("/orders/{orderNo}")
    public String orderDetailPage(HttpServletRequest request,
                                  @PathVariable(value = "orderNo") String orderNo,
                                  HttpSession session) {
        NewBeeMallUserVO userVO = (NewBeeMallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        NewBeeMallOrderDetailVO detailVO = orderService.getOrderDetailByOrderNo(orderNo,userVO.getUserId());
        request.setAttribute("orderDetailVO",detailVO);
        return "mall/order-detail";
    }

    @GetMapping("/orders")
    public String orderListPage(@RequestParam Map<String, Object> params,
                                HttpServletRequest request,
                                HttpSession session) {

        NewBeeMallUserVO userVO = (NewBeeMallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        params.put("userId",userVO.getUserId());

        if (!StringUtils.hasText((String) params.get("page"))) params.put("page", 1);

        params.put("limit", Constants.ORDER_SEARCH_PAGE_LIMIT);

        PageQueryUtil pqu = new PageQueryUtil(params);
        request.setAttribute("orderPageResult", orderService.getMyOrders(pqu));
        request.setAttribute("path","orders");

        return "mall/my-orders";
    }

    @GetMapping("/saveOrder")
    public String saveOrder(HttpSession session) {
        NewBeeMallUserVO userVO = (NewBeeMallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<NewBeeMallShoppingCartItemVO> items =
                cartService.getMyShoppingCartItems(userVO.getUserId());
        if (!StringUtils.hasText(userVO.getAddress()))
            NewBeeMallException.fail(ServiceResultEnum.NULL_ADDRESS_ERROR.getResult());
        if (CollectionUtils.isEmpty(items))
            NewBeeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());

        String orderResult = orderService.saveOrder(userVO, items);

        return "redirect:/orders/" + orderResult;
    }

    @PutMapping("/orders/{orderNo}/canel")
    @ResponseBody
    public Result cancelOrder(@PathVariable(value = "orderNo") String orderNo,
                              HttpSession session) {

        NewBeeMallUserVO userVO = (NewBeeMallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String cancelResult = orderService.cancelOrder(orderNo, userVO.getUserId());

        if (ServiceResultEnum.SUCCESS.getResult().equals(cancelResult))
            return ResultGenerator.genSuccessResult(cancelResult);

        return ResultGenerator.genFailResult(cancelResult);

    }

    @PutMapping("/orders/{orderNo}/finish")
    @ResponseBody
    public Result finishOrder(@PathVariable(value = "orderNo") String orderNo,
                              HttpSession session) {

        NewBeeMallUserVO userVO = (NewBeeMallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String finishResult = orderService.finishOrder(orderNo, userVO.getUserId());

        if (ServiceResultEnum.SUCCESS.getResult().equals(finishResult))
            return ResultGenerator.genSuccessResult(finishResult);
        return ResultGenerator.genFailResult(finishResult);
    }

    @GetMapping("/selectPayType")
    public String selectPayType(HttpServletRequest request,
                                @RequestParam(value = "orderNo") String orderNo,
                                HttpSession session) {

        NewBeeMallUserVO userVO = (NewBeeMallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        NewBeeMallOrder newBeeMallOrder = orderService.getNewBeeMallOrderByOrderNo(orderNo);
        //判断订单userId
        if (!userVO.getUserId().equals(newBeeMallOrder.getUserId()))
            NewBeeMallException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
        //判断订单状态
        if (newBeeMallOrder.getOrderStatus().intValue() != NewBeeMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus())
            NewBeeMallException.fail(ServiceResultEnum.ORDER_STATUS_ERROR.getResult());
        request.setAttribute("orderNo", orderNo);
        request.setAttribute("totalPrice", newBeeMallOrder.getTotalPrice());
        return "mall/pay-select";
    }

    @GetMapping("/payPage")
    public String payOrder(@RequestParam(value = "orderNo") String orderNo,
                           @RequestParam(value = "payType") int payType,
                           HttpServletRequest request,
                           HttpSession session) {
        NewBeeMallUserVO userVO = (NewBeeMallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        NewBeeMallOrder order = orderService.getNewBeeMallOrderByOrderNo(orderNo);
        //判断订单userId
        if (!userVO.getUserId().equals(order.getUserId()))
            NewBeeMallException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
        //判断订单状态
        if (order.getOrderStatus().intValue() != NewBeeMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus())
            NewBeeMallException.fail(ServiceResultEnum.ORDER_STATUS_ERROR.getResult());

        request.setAttribute("orderNo", orderNo);
        request.setAttribute("totalPrice", order.getTotalPrice());
        if (payType == 1) {
            return "mall/alipay";
        } else {
            return "mall/wxpay";
        }
    }

    @GetMapping("/paySuccess")
    @ResponseBody
    public Result paySuccess(@RequestParam(value = "orderNo") String orderNo,
                             @RequestParam(value = "payType") int payType) {
        String payResult = orderService.paySuccess(orderNo, payType);
        if (ServiceResultEnum.SUCCESS.getResult().equals(payResult))
            return ResultGenerator.genSuccessResult(payResult);
        return ResultGenerator.genFailResult(payResult);
    }

}
