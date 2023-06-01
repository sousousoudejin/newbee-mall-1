package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.NewBeeMallCategoryLevelEnum;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.controller.vo.SearchPageCategoryVO;
import ltd.newbee.mall.dao.GoodsCategoryMapper;
import ltd.newbee.mall.entity.GoodsCategory;
import ltd.newbee.mall.service.NewBeeMallGoodsCategoryService;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Service
public class NewBeeMallGoodsCategoryServiceImpl implements NewBeeMallGoodsCategoryService {

    @Resource
    private GoodsCategoryMapper goodsCategoryMapper;

    @Override
    public PageResult getCategorisPage(PageQueryUtil pageQueryUtil) {

        //当前页数据
        List<GoodsCategory> goodsCategorys = goodsCategoryMapper.findGoodsCategoryList(pageQueryUtil);

        //获取总条数
        int totalCount = goodsCategoryMapper.getTotalGoodsCategories(pageQueryUtil);
        return new PageResult(goodsCategorys,totalCount,pageQueryUtil.getLimit(),pageQueryUtil.getPage());
    }

    @Override
    public GoodsCategory getCategoryInfoById(Long categoryId) {
        return goodsCategoryMapper.selectByPrimaryKey(categoryId);
    }

    @Override
    public List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryId) {
        return goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(parentIds,categoryId,0);
    }

    @Override
    public String saveCategory(GoodsCategory goodsCategory) {
        if (goodsCategoryMapper.insertSelective(goodsCategory) > 0) return ServiceResultEnum.SUCCESS.getResult();
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateGoodsCategory(GoodsCategory category) {
        if (goodsCategoryMapper.updateByPrimaryKeySelective(category) > 0) return ServiceResultEnum.SUCCESS.getResult();
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public boolean deleteBatch(Integer[] array) {
        return goodsCategoryMapper.deleteBatch(array);
    }

    @Override
    public SearchPageCategoryVO getCategoriesForSearch(Long categoryId) {

        SearchPageCategoryVO categoryVO = new SearchPageCategoryVO();

        GoodsCategory thirdCategory = goodsCategoryMapper.selectByPrimaryKey(categoryId);

        if (thirdCategory != null && thirdCategory.getCategoryLevel() == NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {

            GoodsCategory secondCategory = goodsCategoryMapper.selectByPrimaryKey(thirdCategory.getParentId());

            if (secondCategory != null && secondCategory.getCategoryLevel() == NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel()) {

                List<GoodsCategory> goodsCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondCategory.getCategoryId()), NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel(), Constants.SEARCH_CATEGORY_NUMBER);

                categoryVO.setCurrentCategoryName(thirdCategory.getCategoryName());

                categoryVO.setSecondLevelCategoryName(secondCategory.getCategoryName());

                categoryVO.setThirdLevelCategoryList(goodsCategories);

                return categoryVO;

            }

        }

        return null;
    }
}
