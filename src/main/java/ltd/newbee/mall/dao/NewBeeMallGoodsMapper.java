package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.NewBeeMallGoods;
import ltd.newbee.mall.util.PageQueryUtil;

import java.util.List;

public interface NewBeeMallGoodsMapper {
    NewBeeMallGoods selectByPrimaryKey(Long goodsId);

    List<NewBeeMallGoods> findNewBeeMallGoodsList(PageQueryUtil pageQueryUtil);

    int getTotalNewBeeMallGoodsBySearch(PageQueryUtil pageQueryUtil);

    int insertSelective(NewBeeMallGoods goods);

    int updateByPrimaryKeySelective(NewBeeMallGoods goods);

    int batchUpdateSellStatus(Long[] orderIds, int sellStatus);

    List<NewBeeMallGoods> selectByPrimaryKeys(List<Long> goodIds);

    List<NewBeeMallGoods> findNewBeeMallGoodsListBySearch(PageQueryUtil pageUtil);
}
