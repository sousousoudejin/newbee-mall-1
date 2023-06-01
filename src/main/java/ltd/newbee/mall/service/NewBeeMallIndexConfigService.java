package ltd.newbee.mall.service;

import ltd.newbee.mall.controller.vo.NewBeeMallIndexConfigGoodsVO;
import ltd.newbee.mall.entity.IndexConfig;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;

import java.util.List;

public interface NewBeeMallIndexConfigService {


    List<NewBeeMallIndexConfigGoodsVO> getConfigGoodsesForIndex(int type, int indexGoodsHotNumber);

    PageResult getConfigsPage(PageQueryUtil pqu);

    String saveIndexConfig(IndexConfig indexConfig);

    String updateIndexConfig(IndexConfig indexConfig);

    IndexConfig getIndexConfigById(Long id);

    boolean deleteBatch(Long[] ids);
}
