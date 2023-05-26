package ltd.newbee.mall.dao;

import ltd.newbee.mall.common.NewBeeMallCategoryLevelEnum;
import ltd.newbee.mall.entity.Carousel;
import ltd.newbee.mall.entity.GoodsCategory;
import ltd.newbee.mall.util.PageQueryUtil;

import java.util.List;

public interface CarouselMapper {
    List<Carousel> findCarouselList(PageQueryUtil page);

    int getTotalCarousels();

    int insertSelective(Carousel carousel);

    int updateByPrimaryKeySelective(Carousel carousel);

    Carousel selectByPrimaryKey(Integer id);

    boolean deleteBatch(Integer[] ids);

    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel, int number);

    List<Carousel> findCarouselsByNum(int number);
}
