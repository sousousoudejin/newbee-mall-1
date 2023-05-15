package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.dao.CarouselMapper;
import ltd.newbee.mall.entity.Carousel;
import ltd.newbee.mall.service.NewBeeMallCarouselService;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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
}
