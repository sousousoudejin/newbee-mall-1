package ltd.newbee.mall.service;

import ltd.newbee.mall.controller.vo.NewBeeMallIndexCarouselVO;
import ltd.newbee.mall.controller.vo.NewBeeMallIndexCategoryVO;
import ltd.newbee.mall.entity.Carousel;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;

import java.util.List;

public interface NewBeeMallCarouselService {
    PageResult getCarouselPage(PageQueryUtil page);

    String saveCarousel(Carousel carousel);

    String updateCarousel(Carousel carousel);

    Carousel getCarouselById(Integer id);

    boolean deleteBatch(Integer[] ids);

    List<NewBeeMallIndexCategoryVO> getCategoriesForIndex();

    List<NewBeeMallIndexCarouselVO> getCarouselsForIndex(int indexCarouselNumber);
}
