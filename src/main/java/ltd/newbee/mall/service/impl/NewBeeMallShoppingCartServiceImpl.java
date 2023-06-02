package ltd.newbee.mall.service.impl;

import cn.hutool.core.bean.BeanUtil;
import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.controller.vo.NewBeeMallShoppingCartItemVO;
import ltd.newbee.mall.dao.NewBeeMallGoodsMapper;
import ltd.newbee.mall.dao.NewBeeMallShoppingCartItemMapper;
import ltd.newbee.mall.entity.NewBeeMallGoods;
import ltd.newbee.mall.entity.NewBeeMallShoppingCartItem;
import ltd.newbee.mall.service.NewBeeMallShoppingCartService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NewBeeMallShoppingCartServiceImpl implements NewBeeMallShoppingCartService {

    @Resource
    private NewBeeMallShoppingCartItemMapper itemMapper;

    @Resource
    private NewBeeMallGoodsMapper goodsMapper;

    @Override
    public List<NewBeeMallShoppingCartItemVO> getMyShoppingCartItems(Long userId) {
        List<NewBeeMallShoppingCartItemVO> itemVOS = new ArrayList<>();
        List<NewBeeMallShoppingCartItem> items = itemMapper.selectByUserId(userId, Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);

        if (!CollectionUtils.isEmpty(items)) {

            List<Long> goodsIds = items.stream().map(NewBeeMallShoppingCartItem::getGoodsId).collect(Collectors.toList());
            List<NewBeeMallGoods> newBeeMallGoods = goodsMapper.selectByPrimaryKeys(goodsIds);

            Map<Long, NewBeeMallGoods> goodsMap = new HashMap<>();

            if (!CollectionUtils.isEmpty(newBeeMallGoods)) {

                goodsMap = newBeeMallGoods.stream().collect(Collectors.toMap(NewBeeMallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));

            }

            for (NewBeeMallGoods newBeeMallGood : newBeeMallGoods) {

                NewBeeMallShoppingCartItemVO c = new NewBeeMallShoppingCartItemVO();
                BeanUtil.copyProperties(newBeeMallGood,c);

                if (goodsMap.containsKey(newBeeMallGood.getGoodsId())) {

                    NewBeeMallGoods goods = goodsMap.get(newBeeMallGood.getGoodsId());

                    c.setGoodsCoverImg(goods.getGoodsCoverImg());
                    String goodsName = goods.getGoodsName();
                    if (goodsName.length() > 28) goodsName = goodsName.substring(0,28) + "..";

                    c.setGoodsName(goodsName);
                    c.setSellingPrice(goods.getSellingPrice());

                    itemVOS.add(c);
                }

            }


        }
        return itemVOS;
    }

    @Override
    public String saveNewBeeMallCartItem(NewBeeMallShoppingCartItem cartItem) {
        NewBeeMallShoppingCartItem temp = itemMapper.selectByUserIdAndGoodsId(cartItem.getUserId(), cartItem.getGoodsId());
        if (temp != null) {
            temp.setGoodsCount(cartItem.getGoodsCount());
            return updateNewBeeMallCartItem(temp);
        }
        NewBeeMallGoods goods = goodsMapper.selectByPrimaryKey(cartItem.getGoodsId());
        if (goods == null) return ServiceResultEnum.GOODS_NOT_EXIST.getResult();

        int totalItem = itemMapper.selectCountByUserId(cartItem.getUserId()) + 1;

        //超出单个商品的最大数量
        if (cartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER)
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        //超出最大数量
        if (totalItem > Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER)
            return ServiceResultEnum.SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR.getResult();
//保存记录
        if (itemMapper.insertSelective(cartItem) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateNewBeeMallCartItem(NewBeeMallShoppingCartItem temp) {

        NewBeeMallShoppingCartItem item = itemMapper.selectByPrimaryKey(temp.getCartItemId());

        if (item == null)
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();

        if (item.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER)
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();

        if (!temp.getUserId().equals(item.getUserId()))
            return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();

        if (temp.getGoodsCount().equals(item.getGoodsCount()))
            return ServiceResultEnum.SUCCESS.getResult();

        item.setGoodsCount(temp.getGoodsCount());
        item.setUpdateTime(new Date());

        if (itemMapper.updateByPrimaryKeySelective(item) > 0)
            return ServiceResultEnum.SUCCESS.getResult();

        return ServiceResultEnum.DB_ERROR.getResult();

    }

    @Override
    public Boolean deleteById(Long itemId, Long userId) {
        NewBeeMallShoppingCartItem newBeeMallShoppingCartItem = itemMapper.selectByPrimaryKey(itemId);
        if (newBeeMallShoppingCartItem == null) {
            return false;
        }
        //userId不同不能删除
        if (!userId.equals(newBeeMallShoppingCartItem.getUserId())) {
            return false;
        }
        return itemMapper.deleteByPrimaryKey(itemId) > 0;
    }


}
