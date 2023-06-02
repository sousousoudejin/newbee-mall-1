package ltd.newbee.mall.service;

import ltd.newbee.mall.controller.vo.NewBeeMallShoppingCartItemVO;
import ltd.newbee.mall.entity.NewBeeMallShoppingCartItem;

import java.util.List;

public interface NewBeeMallShoppingCartService {
    List<NewBeeMallShoppingCartItemVO> getMyShoppingCartItems(Long userId);

    String saveNewBeeMallCartItem(NewBeeMallShoppingCartItem cartItem);

    String updateNewBeeMallCartItem(NewBeeMallShoppingCartItem cartItem);

    Boolean deleteById(Long itemId, Long userId);
}
