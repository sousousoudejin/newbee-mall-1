package ltd.newbee.mall.controller.admin;

import ltd.newbee.mall.common.NewBeeMallCategoryLevelEnum;
import ltd.newbee.mall.common.NewBeeMallException;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.entity.GoodsCategory;
import ltd.newbee.mall.service.NewBeeMallGoodsCategoryService;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class NewBeeMallGoodsCategoryController {

    @Resource
    private NewBeeMallGoodsCategoryService categoryService;

    @GetMapping("/categories")
    public String categorysPage(HttpServletRequest request,
                                @RequestParam("categoryLevel") Integer categoryLevel,
                                @RequestParam("parentId") Integer parentId,
                                @RequestParam("backParentId") Integer backParentId) {
        if (categoryLevel == null || categoryLevel < 1 || categoryLevel > 3) NewBeeMallException.fail("参数异常!");

        request.setAttribute("categoryLevel",categoryLevel);
        request.setAttribute("parentId",parentId);
        request.setAttribute("backParentId",backParentId);
        request.setAttribute("path","newbee_mall_category");

        return "admin/newbee_mall_category";
    }

    @GetMapping("/categories/list")
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> param) {
        if (!StringUtils.hasText((String) param.get("page")) ||
        !StringUtils.hasText((String) param.get("limit"))) return ResultGenerator.genFailResult("参数异常!");

        PageQueryUtil pageQueryUtil = new PageQueryUtil(param);
        return ResultGenerator.genSuccessResult(categoryService.getCategorisPage(pageQueryUtil));
    }

    @GetMapping("/categories/listForSelect")
    @ResponseBody
    public Result listForSelect(@RequestParam Long categoryId) {

        if (categoryId == null || categoryId < 1) return ResultGenerator.genFailResult("参数异常!");

        GoodsCategory goodsCategory = categoryService.getCategoryInfoById(categoryId);

        if (goodsCategory == null ||
                goodsCategory.getCategoryLevel()
                        == NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel()) return ResultGenerator.genFailResult("参数异常!");

        HashMap<String, Object> category = new HashMap<>();

        if (goodsCategory.getCategoryLevel() ==
        NewBeeMallCategoryLevelEnum.LEVEL_ONE.getLevel()) {

            List<GoodsCategory> secondList = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(goodsCategory.getCategoryId()), NewBeeMallCategoryLevelEnum.LEVEL_ONE.getLevel());

            if (!CollectionUtils.isEmpty(secondList)) {

                List<GoodsCategory> thirdList = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondList.get(0).getCategoryId()), NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel());

                category.put("secondLevelCategories",secondList);
                category.put("thirdLevelCategories",thirdList);
            }

        }

        if (goodsCategory.getCategoryLevel() ==
        NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel()) {

            List<GoodsCategory> thirdList = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(goodsCategory.getCategoryId()), NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel());
            category.put("thirdLevelCategories", thirdList);
        }

        return ResultGenerator.genSuccessResult(category);

    }

    @PostMapping("/categories/save")
    @ResponseBody
    public Result addGoodsCategory(@RequestBody GoodsCategory goodsCategory) {

        if (Objects.isNull(goodsCategory.getCategoryLevel()) ||
        !StringUtils.hasText(goodsCategory.getCategoryName()) ||
        Objects.isNull(goodsCategory.getParentId()) ||
        Objects.isNull(goodsCategory.getCategoryRank())) return ResultGenerator.genFailResult("参数异常");

        String result = categoryService.saveCategory(goodsCategory);

        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) return ResultGenerator.genSuccessResult(result);

        return ResultGenerator.genFailResult(result);

    }

    @PostMapping("/categories/update")
    @ResponseBody
    public Result update(@RequestBody GoodsCategory category){

        if (Objects.isNull(category.getCategoryId()) ||
        Objects.isNull(category.getCategoryLevel()) ||
        !StringUtils.hasText(category.getCategoryName()) ||
        Objects.isNull(category.getParentId()) ||
        Objects.isNull(category.getCategoryRank())) return ResultGenerator.genFailResult("参数异常!");

        String result = categoryService.updateGoodsCategory(category);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) return ResultGenerator.genSuccessResult();
        return ResultGenerator.genFailResult(result);

    }

    @GetMapping("/categories/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Long categoryId) {

        if (Objects.isNull(categoryId) || categoryId < 1) return ResultGenerator.genFailResult("参数异常！");

        GoodsCategory categoryInfoById = categoryService.getCategoryInfoById(categoryId);

        if (categoryInfoById == null) return ResultGenerator.genFailResult("未查到数据！");

        return ResultGenerator.genSuccessResult(categoryInfoById);

    }

    @PostMapping("/categories/delete")
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids) {

        if (ids.length < 0) return ResultGenerator.genFailResult("参数异常！");

        if (categoryService.deleteBatch(ids)) return ResultGenerator.genSuccessResult();
        return ResultGenerator.genFailResult("删除失败！");



    }
}
