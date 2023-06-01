package ltd.newbee.mall.service;

import ltd.newbee.mall.controller.vo.SearchPageCategoryVO;
import ltd.newbee.mall.entity.GoodsCategory;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;

import java.util.List;

public interface NewBeeMallGoodsCategoryService {
    PageResult getCategorisPage(PageQueryUtil pageQueryUtil);

    GoodsCategory getCategoryInfoById(Long categoryId);

    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> integers, int level);

    String saveCategory(GoodsCategory goodsCategory);

    String updateGoodsCategory(GoodsCategory category);

    boolean deleteBatch(Integer[] ids);

    SearchPageCategoryVO getCategoriesForSearch(Long categoryId);
}
