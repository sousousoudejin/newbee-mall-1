package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.GoodsCategory;
import ltd.newbee.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsCategoryMapper {
    List<GoodsCategory> findGoodsCategoryList(PageQueryUtil pageQueryUtil);

    int getTotalGoodsCategories(PageQueryUtil pageQueryUtil);

    GoodsCategory selectByPrimaryKey(Long categoryId);

    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(@Param("parentIds") List<Long> parentIds, @Param("categoryLevel") int categoryLevel, @Param("number") int number);

    int insertSelective(GoodsCategory goodsCategory);

    int updateByPrimaryKeySelective(GoodsCategory category);

    boolean deleteBatch(Integer[] array);
}
