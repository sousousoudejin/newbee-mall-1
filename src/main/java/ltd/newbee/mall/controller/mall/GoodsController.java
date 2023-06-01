package ltd.newbee.mall.controller.mall;

import cn.hutool.core.bean.BeanUtil;
import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.NewBeeMallException;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.controller.vo.NewBeeMallGoodsDetailVO;
import ltd.newbee.mall.controller.vo.SearchPageCategoryVO;
import ltd.newbee.mall.entity.NewBeeMallGoods;
import ltd.newbee.mall.service.NewBeeMallGoodsCategoryService;
import ltd.newbee.mall.service.NewBeeMallGoodsService;
import ltd.newbee.mall.util.PageQueryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class GoodsController {

    @Autowired
    private NewBeeMallGoodsService goodsService;

    @Autowired
    private NewBeeMallGoodsCategoryService categoryService;

    @GetMapping("/search")
    public String searchPage(@RequestParam Map<String, Object> params, HttpServletRequest request) {

        if (!StringUtils.hasText((String) params.get("page"))) params.put("page",1);

        params.put("limit", Constants.GOODS_SEARCH_PAGE_LIMIT);

        //处理搜索页面分类数据
        if(params.containsKey("goodsCategoryId")
                && StringUtils.hasText((String) params.get("goodsCategoryId"))) {

            Long categoryId =  (Long) params.get("goodsCategoryId");

            SearchPageCategoryVO categoryVO = categoryService.getCategoriesForSearch(categoryId);

            request.setAttribute("goodsCategoryId",categoryId);

            request.setAttribute("searchPageCategoryVO",categoryVO);

        }

        if (params.containsKey("orderBy")
                && StringUtils.hasText((String) params.get("orderBy"))) {

            request.setAttribute("orderBy",params.get("orderBy"));

        }

        String keyword = "";

        if (params.containsKey("keyword")
                && StringUtils.hasText((String) params.get("keyword"))) {
            keyword = params.get("keyword").toString();
        }

        request.setAttribute("keyword",keyword);

        params.put("keyword",keyword);

        params.put("goodsSellStatus",Constants.SELL_STATUS_UP);

        PageQueryUtil pageUtil = new PageQueryUtil(params);

        request.setAttribute("pageResult",goodsService.searchNewBeeMallGoods(pageUtil));

        return "mall/search";

    }


    @GetMapping("/goods/detail/{goodsId}")
    public String detailPage(@PathVariable("goodsId") Long goodsId, HttpServletRequest request) {

        if (goodsId < 1) NewBeeMallException.fail("参数异常!");

        NewBeeMallGoods goods = goodsService.getNewBeeMallGoodsById(goodsId);

        if (goods.getGoodsSellStatus() != Constants.SELL_STATUS_UP) NewBeeMallException.fail(ServiceResultEnum.GOODS_PUT_DOWN.getResult());

        NewBeeMallGoodsDetailVO detailVO = new NewBeeMallGoodsDetailVO();

        BeanUtil.copyProperties(goods, detailVO);

        detailVO.setGoodsCarouselList(goods.getGoodsCarousel().split(","));

        request.setAttribute("goodsDetail",detailVO);

        return "mall/detail";

    }

}
