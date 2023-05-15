package ltd.newbee.mall.controller.admin;

import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.entity.Carousel;
import ltd.newbee.mall.service.NewBeeMallCarouselService;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/admin")
public class NewBeeMallCarouselController {

    @Resource
    private NewBeeMallCarouselService carouselService;

    @GetMapping("/carousels")
    public String carouselPage(HttpServletRequest request) {
        request.setAttribute("path","newbee_mall_carousel");
        return "admin/newbee_mall_carousel";
    }

    /**
     * 轮播图列表
     * @param param
     * @return
     */
    @GetMapping("/carousels/list")
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> param) {

        if (!StringUtils.hasText((String) param.get("page"))
                || !StringUtils.hasText((String) param.get("limit")))
            return ResultGenerator.genFailResult("参数异常!");
        PageQueryUtil page = new PageQueryUtil(param);

        return ResultGenerator.genSuccessResult(carouselService.getCarouselPage(page));

    }


    /**
     * 添加轮播图
     * @param carousel
     * @return
     */
    @PostMapping("/carousels/save")
    @ResponseBody
    public Result save(@RequestBody Carousel carousel) {

        if (!StringUtils.hasText(carousel.getCarouselUrl())
                || Objects.isNull(carousel.getCarouselRank()))
            return ResultGenerator.genFailResult("参数异常！");

        String result = carouselService.saveCarousel(carousel);

        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } return ResultGenerator.genFailResult(result);

    }

    /**
     * 更新 修改 轮播图
     * @param carousel
     * @return
     */
    @PostMapping("/carousels/update")
    @ResponseBody
    public Result update(@RequestBody Carousel carousel) {

        if (Objects.isNull(carousel.getCarouselId()) ||
        !StringUtils.hasText(carousel.getCarouselUrl())||
        Objects.isNull(carousel.getCarouselRank())) return ResultGenerator.genFailResult("参数异常！");

        String result = carouselService.updateCarousel(carousel);

        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult(result);
        } return ResultGenerator.genFailResult(result);

    }

    /**
     * 详情 轮播图
     * @param id
     * @return
     */
    @GetMapping("/carousels/info/{id}")
    @ResponseBody
    public Result info(@PathVariable Integer id) {

        Carousel carousel = carouselService.getCarouselById(id);

        if (carousel == null) return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());

            return ResultGenerator.genSuccessResult(carousel);
    }

    /**
     * 删除 轮播图
     * @param ids
     * @return
     */
    @PostMapping("/carousels/delete")
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids) {

        if (ids.length < 1) return ResultGenerator.genFailResult("参数异常！");

        if (carouselService.deleteBatch(ids)) return ResultGenerator.genSuccessResult();

        return ResultGenerator.genFailResult("删除失败!");

    }



}
