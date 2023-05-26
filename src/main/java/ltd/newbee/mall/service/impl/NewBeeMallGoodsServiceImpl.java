package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.dao.NewBeeMallGoodsMapper;
import ltd.newbee.mall.entity.NewBeeMallGoods;
import ltd.newbee.mall.service.NewBeeMallGoodsService;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class NewBeeMallGoodsServiceImpl implements NewBeeMallGoodsService {

    @Resource
    private NewBeeMallGoodsMapper goodsMapper;


    @Override
    public NewBeeMallGoods getNewBeeMallGoodsById(Long goodsId) {
        return goodsMapper.selectByPrimaryKey(goodsId);
    }

    @Override
    public PageResult getNewBeeMallGoodsPage(PageQueryUtil pageQueryUtil) {

        //当前页记录
        List<NewBeeMallGoods> goodsList = goodsMapper.findNewBeeMallGoodsList(pageQueryUtil);

        //获取总条数
        int totalCount = goodsMapper.getTotalNewBeeMallGoodsBySearch();
        return new PageResult(goodsList,totalCount,pageQueryUtil.getLimit(),pageQueryUtil.getPage());
    }

    @Override
    public String saveNewBeeMallGoods(NewBeeMallGoods goods) {

        if (goodsMapper.insertSelective(goods) > 0) return ServiceResultEnum.SUCCESS.getResult();

        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateNewBeeMallGoods(NewBeeMallGoods goods) {
        if (goodsMapper.updateByPrimaryKeySelective(goods) > 0) return ServiceResultEnum.SUCCESS.getResult();

        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public boolean batchUpdateSellStatus(Long[] ids, int sellStatus) {
        return goodsMapper.batchUpdateSellStatus(ids,sellStatus) > 0;
    }
}
