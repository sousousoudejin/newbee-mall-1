package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.IndexConfig;

import java.util.List;

public interface IndexConfigMapper {
    List<IndexConfig> findIndexConfigsByTypeAndNum(int configType, int number);
}
