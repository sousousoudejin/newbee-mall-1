package ltd.newbee.mall.service.impl;

import cn.hutool.core.bean.BeanUtil;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.controller.vo.NewBeeMallIndexConfigGoodsVO;
import ltd.newbee.mall.dao.IndexConfigMapper;
import ltd.newbee.mall.dao.NewBeeMallGoodsMapper;
import ltd.newbee.mall.entity.IndexConfig;
import ltd.newbee.mall.entity.NewBeeMallGoods;
import ltd.newbee.mall.service.NewBeeMallIndexConfigService;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;
import ltd.newbee.mall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewBeeMallIndexConfigServiceImpl implements NewBeeMallIndexConfigService {

    @Autowired
    private IndexConfigMapper indexConfigMapper;

    @Autowired
    private NewBeeMallGoodsMapper goodsMapper;

    @Override
    public List<NewBeeMallIndexConfigGoodsVO> getConfigGoodsesForIndex(int type, int indexGoodsHotNumber) {

        List<NewBeeMallIndexConfigGoodsVO> indexConfigGoodsVOS = new ArrayList<>(indexGoodsHotNumber);
        List<IndexConfig> indexConfigs = indexConfigMapper.findIndexConfigsByTypeAndNum(type,indexGoodsHotNumber);

        if (!CollectionUtils.isEmpty(indexConfigs)) {

            List<Long> goodIds = indexConfigs.stream().map(IndexConfig::getGoodsId).collect(Collectors.toList());

            List<NewBeeMallGoods> newBeeMallGoods = goodsMapper.selectByPrimaryKeys(goodIds);
            indexConfigGoodsVOS = BeanUtil.copyToList(newBeeMallGoods, NewBeeMallIndexConfigGoodsVO.class);
            for (NewBeeMallIndexConfigGoodsVO indexConfigGoodsVO : indexConfigGoodsVOS) {
                String goodsName = indexConfigGoodsVO.getGoodsName();
                String goodsIntro = indexConfigGoodsVO.getGoodsIntro();

                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 30) {
                    goodsName = goodsName.substring(0, 30) + "...";
                    indexConfigGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 22) {
                    goodsIntro = goodsIntro.substring(0, 22) + "...";
                    indexConfigGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }

        return indexConfigGoodsVOS;
    }

    @Override
    public PageResult getConfigsPage(PageQueryUtil pqu) {

        List<IndexConfig> configs = indexConfigMapper.findIndexConfigList(pqu);

        int total = indexConfigMapper.getTotalIndexConfigs(pqu);

        PageResult pageResult = new PageResult(configs, total, pqu.getLimit(), pqu.getPage());

        return pageResult;
    }

    @Override
    public String saveIndexConfig(IndexConfig indexConfig) {

        if (goodsMapper.selectByPrimaryKey(indexConfig.getGoodsId())
                == null) return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        if (indexConfigMapper.selectByTypeAndGoodsId(indexConfig.getConfigType(), indexConfig.getGoodsId())
                != null) return ServiceResultEnum.SAME_INDEX_CONFIG_EXIST.getResult();
        if (indexConfigMapper.insertSelective(indexConfig) > 0) return ServiceResultEnum.SUCCESS.getResult();

        return ServiceResultEnum.DB_ERROR.getResult();

    }

    @Override
    public String updateIndexConfig(IndexConfig indexConfig) {

        if (goodsMapper.selectByPrimaryKey(indexConfig.getGoodsId())
                == null) return ServiceResultEnum.GOODS_NOT_EXIST.getResult();

        IndexConfig temp0 = indexConfigMapper.selectByPrimaryKey(indexConfig.getConfigId());

        if (temp0 == null) return ServiceResultEnum.DATA_NOT_EXIST.getResult();

        IndexConfig temp1 = indexConfigMapper.selectByTypeAndGoodsId(indexConfig.getConfigType(), indexConfig.getGoodsId());

        if (temp1 != null && !temp1.getConfigId()
                        .equals(indexConfig.getConfigId())) return ServiceResultEnum.SAME_INDEX_CONFIG_EXIST.getResult();

        indexConfig.setUpdateTime(new Date());

        if (indexConfigMapper.updateByPrimaryKeySelective(indexConfig)
                > 0) return ResultGenerator.genSuccessResult().getMessage();

        return ServiceResultEnum.ERROR.getResult();
    }

    @Override
    public IndexConfig getIndexConfigById(Long configId) {
        return indexConfigMapper.selectByPrimaryKey(configId);
    }

    @Override
    public boolean deleteBatch(Long[] ids) {
        if (ids.length < 1) return false;
        return indexConfigMapper.deleteBatch(ids) > 0;
    }
}
