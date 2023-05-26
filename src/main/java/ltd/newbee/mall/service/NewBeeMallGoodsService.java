package ltd.newbee.mall.service;

import ltd.newbee.mall.entity.NewBeeMallGoods;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;

public interface NewBeeMallGoodsService {
    NewBeeMallGoods getNewBeeMallGoodsById(Long goodsId);

    PageResult getNewBeeMallGoodsPage(PageQueryUtil pageQueryUtil);

    String saveNewBeeMallGoods(NewBeeMallGoods goods);

    String updateNewBeeMallGoods(NewBeeMallGoods goods);

    boolean batchUpdateSellStatus(Long[] ids, int sellStatus);
}
