package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.NewBeeMallShoppingCartItem;

import java.util.List;

public interface NewBeeMallShoppingCartItemMapper {
    List<NewBeeMallShoppingCartItem> selectByUserId(Long newBeeMallUserId, int number);

    NewBeeMallShoppingCartItem selectByUserIdAndGoodsId(Long userId, Long goodsId);

    int selectCountByUserId(Long userId);

    int insertSelective(NewBeeMallShoppingCartItem cartItem);

    NewBeeMallShoppingCartItem selectByPrimaryKey(Long cartItemId);

    int updateByPrimaryKeySelective(NewBeeMallShoppingCartItem item);

    int deleteByPrimaryKey(Long itemId);

    int deleteBatch(List<Long> cartItemIds);
}
