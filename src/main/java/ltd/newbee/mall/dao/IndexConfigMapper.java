package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.IndexConfig;
import ltd.newbee.mall.util.PageQueryUtil;

import java.util.List;

public interface IndexConfigMapper {
    List<IndexConfig> findIndexConfigsByTypeAndNum(int configType, int number);

    List<IndexConfig> findIndexConfigList(PageQueryUtil pqu);


    int getTotalIndexConfigs(PageQueryUtil pqu);

    IndexConfig selectByTypeAndGoodsId(Byte configType, Long goodsId);

    int insertSelective(IndexConfig indexConfig);

    IndexConfig selectByPrimaryKey(Long configId);

    int updateByPrimaryKeySelective(IndexConfig indexConfig);

    int deleteBatch(Long[] ids);
}
