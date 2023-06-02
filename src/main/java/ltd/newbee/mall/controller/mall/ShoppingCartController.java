package ltd.newbee.mall.controller.mall;

import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.NewBeeMallException;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.controller.vo.NewBeeMallShoppingCartItemVO;
import ltd.newbee.mall.controller.vo.NewBeeMallUserVO;
import ltd.newbee.mall.entity.NewBeeMallShoppingCartItem;
import ltd.newbee.mall.service.NewBeeMallShoppingCartService;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ShoppingCartController {

    @Autowired
    private NewBeeMallShoppingCartService cartService;

    @GetMapping("/shop-cart")
    public String cartListPage(HttpServletRequest request,
                               HttpSession session) {

        NewBeeMallUserVO userVO = (NewBeeMallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);

        int itemsTotal = 0;
        int priceTotal = 0;

        List<NewBeeMallShoppingCartItemVO> itemVOList = cartService.getMyShoppingCartItems(userVO.getUserId());

        if (!CollectionUtils.isEmpty(itemVOList)) {

            itemsTotal = itemVOList.stream().mapToInt(NewBeeMallShoppingCartItemVO::getGoodsCount).sum();

            if (itemsTotal < 1) NewBeeMallException.fail("购物项不能为空!");

            for (NewBeeMallShoppingCartItemVO cartItemVO : itemVOList) {
                priceTotal += cartItemVO.getGoodsCount() * cartItemVO.getSellingPrice();
            }

            if (priceTotal < 1) NewBeeMallException.fail("购物项价格异常!");

        }

        request.setAttribute("itemsTotal",itemsTotal);
        request.setAttribute("priceTotal",priceTotal);
        request.setAttribute("myShoppingCartItems",itemVOList);

        return "mall/cart";
    }

    @PostMapping("/shop-cart")
    @ResponseBody
    public Result saveNewBeeMallShoppingCartItem(@RequestBody NewBeeMallShoppingCartItem cartItem,
                                                 HttpSession session) {

        NewBeeMallUserVO userVO = (NewBeeMallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        cartItem.setUserId(userVO.getUserId());

        String result = cartService.saveNewBeeMallCartItem(cartItem);

        if (result.equals(ServiceResultEnum.SUCCESS.getResult())) return ResultGenerator.genSuccessResult(result);

        return ResultGenerator.genFailResult(result);

    }

    @PostMapping("/shop-cart")
    @ResponseBody
    public Result updateNewBeeMallShoppingCartItem(@RequestBody NewBeeMallShoppingCartItem cartItem,
                                                   HttpSession session) {
        NewBeeMallUserVO userVO = (NewBeeMallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        cartItem.setUserId(userVO.getUserId());
        String result = cartService.updateNewBeeMallCartItem(cartItem);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result))
            return ResultGenerator.genSuccessResult(result);
        return ResultGenerator.genFailResult(result);
    }

    @GetMapping("/shop-cart/{newBeeMallShoppingCartItemId}")
    @ResponseBody
    public Result updateNewBeeMallShoppingCartItem(@PathVariable(value = "newBeeMallShoppingCartItemId") Long itemId,
                                                    HttpSession session) {
        NewBeeMallUserVO userVO = (NewBeeMallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Boolean deleteResult = cartService.deleteById(itemId, userVO.getUserId());

        if (deleteResult)
            return ResultGenerator.genSuccessResult();
        return ResultGenerator.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());
    }



}
