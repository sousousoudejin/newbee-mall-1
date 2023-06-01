package ltd.newbee.mall.service.impl;

import cn.hutool.core.bean.BeanUtil;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.controller.vo.NewBeeMallSearchGoodsVO;
import ltd.newbee.mall.dao.NewBeeMallGoodsMapper;
import ltd.newbee.mall.entity.NewBeeMallGoods;
import ltd.newbee.mall.service.NewBeeMallGoodsService;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
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
        int totalCount = goodsMapper.getTotalNewBeeMallGoodsBySearch(pageQueryUtil);
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

    @Override
    public PageResult searchNewBeeMallGoods(PageQueryUtil pageUtil) {
        List<NewBeeMallGoods> goodsList = goodsMapper.findNewBeeMallGoodsListBySearch(pageUtil);
        int total = goodsMapper.getTotalNewBeeMallGoodsBySearch(pageUtil);
        List<NewBeeMallSearchGoodsVO> newBeeMallSearchGoodsVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            newBeeMallSearchGoodsVOS = BeanUtil.copyToList(goodsList,NewBeeMallSearchGoodsVO.class);
            for (NewBeeMallSearchGoodsVO goodsVO : newBeeMallSearchGoodsVOS) {
                String goodsName = goodsVO.getGoodsName();
                String goodsIntro = goodsVO.getGoodsIntro();
                if (goodsName.length() > 28) goodsName = goodsName.substring(0,28) + "..";

                if (goodsIntro.length() > 30) goodsIntro = goodsIntro.substring(0,30) + "..";

                goodsVO.setGoodsName(goodsName);
                goodsVO.setGoodsIntro(goodsIntro);
            }
        }
        PageResult pageResult = new PageResult(newBeeMallSearchGoodsVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
}
