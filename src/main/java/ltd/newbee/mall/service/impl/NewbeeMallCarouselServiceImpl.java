package ltd.newbee.mall.service.impl;

import cn.hutool.core.bean.BeanUtil;
import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.NewBeeMallCategoryLevelEnum;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.controller.vo.NewBeeMallIndexCarouselVO;
import ltd.newbee.mall.controller.vo.NewBeeMallIndexCategoryVO;
import ltd.newbee.mall.controller.vo.SecondLevelCategoryVO;
import ltd.newbee.mall.controller.vo.ThirdLevelCategoryVO;
import ltd.newbee.mall.dao.CarouselMapper;
import ltd.newbee.mall.entity.Carousel;
import ltd.newbee.mall.entity.GoodsCategory;
import ltd.newbee.mall.service.NewBeeMallCarouselService;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class NewbeeMallCarouselServiceImpl implements NewBeeMallCarouselService {

    @Resource
    private CarouselMapper carouselMapper;


    @Override
    public PageResult getCarouselPage(PageQueryUtil page) {

        //获取当前页数据
        List<Carousel> carousels = carouselMapper.findCarouselList(page);

        //获取总条数 用来计算总共多少页
        int total = carouselMapper.getTotalCarousels();

        return new PageResult(carousels,total,page.getLimit(),page.getPage());
    }

    @Override
    public String saveCarousel(Carousel carousel) {
        if (carouselMapper.insertSelective(carousel) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        } return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateCarousel(Carousel carousel) {
        if (carouselMapper.updateByPrimaryKeySelective(carousel) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        } return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public Carousel getCarouselById(Integer id) {
        return carouselMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean deleteBatch(Integer[] ids) {
        return carouselMapper.deleteBatch(ids);
    }

    @Override
    public List<NewBeeMallIndexCategoryVO> getCategoriesForIndex() {

        List<NewBeeMallIndexCategoryVO> categoryVOS = new ArrayList<>();
        //获取一集分类数据
        List<GoodsCategory> goodsCategories = carouselMapper.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), NewBeeMallCategoryLevelEnum.LEVEL_ONE.getLevel(), Constants.INDEX_CATEGORY_NUMBER);
        if (!CollectionUtils.isEmpty(goodsCategories)) {

            List<Long> firsts = goodsCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
            //获取二级分类数据
            List<GoodsCategory> secondGoodCategories = carouselMapper.selectByLevelAndParentIdsAndNumber(firsts,NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel(),0);
            if (!CollectionUtils.isEmpty(secondGoodCategories)) {

                List<Long> seconds = secondGoodCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
                //获取三级分类数据
                List<GoodsCategory> thirdGoodCategories = carouselMapper.selectByLevelAndParentIdsAndNumber(seconds, NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel(), 0);

                if (!CollectionUtils.isEmpty(thirdGoodCategories)) {

                    Map<Long, List<GoodsCategory>> thirdLevelCategoryMap = thirdGoodCategories.stream().collect(groupingBy(GoodsCategory::getParentId));

                    List<SecondLevelCategoryVO> secondCategories = new ArrayList<>();
                    //处理二级分类数据
                    for (GoodsCategory secondGoodCategory : secondGoodCategories) {
                        SecondLevelCategoryVO secondLevelCategoryVO = new SecondLevelCategoryVO();
                        BeanUtil.copyProperties(secondGoodCategory,secondLevelCategoryVO);

                        if (thirdLevelCategoryMap.containsKey(secondLevelCategoryVO.getCategoryId())) {

                            List<GoodsCategory> thirdCategory = thirdLevelCategoryMap.get(secondLevelCategoryVO.getCategoryId());
                            secondLevelCategoryVO.setThirdLevelCategoryVOS(BeanUtil.copyToList(thirdCategory, ThirdLevelCategoryVO.class));
                            secondCategories.add(secondLevelCategoryVO);

                        }
                    }

                    //处理一级分类数据
                    if (!CollectionUtils.isEmpty(secondCategories)) {

                        Map<Long, List<SecondLevelCategoryVO>> firstCategories = secondCategories.stream().collect(groupingBy(SecondLevelCategoryVO::getParentId));

                        for (GoodsCategory goodsCategory : goodsCategories) {
                            NewBeeMallIndexCategoryVO firstCategory = new NewBeeMallIndexCategoryVO();
                            if (firstCategories.containsKey(goodsCategory.getCategoryId())) {
                                firstCategory.setSecondLevelCategoryVOS(firstCategories.get(goodsCategory.getCategoryId()));
                                categoryVOS.add(firstCategory);
                            }

                        }

                    }

                }
            }

        }
        return categoryVOS;

    }

    @Override
    public List<NewBeeMallIndexCarouselVO> getCarouselsForIndex(int number) {
        List<NewBeeMallIndexCarouselVO> newBeeMallIndexCarouselVOS = new ArrayList<>(number);
        List<Carousel> carousels = carouselMapper.findCarouselsByNum(number);
        if (!CollectionUtils.isEmpty(carousels)) {
            newBeeMallIndexCarouselVOS = BeanUtil.copyToList(carousels, NewBeeMallIndexCarouselVO.class);
        }
        return newBeeMallIndexCarouselVOS;
    }
}
