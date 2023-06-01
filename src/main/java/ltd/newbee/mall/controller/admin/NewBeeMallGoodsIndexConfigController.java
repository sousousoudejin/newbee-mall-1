package ltd.newbee.mall.controller.admin;

import cn.hutool.core.util.ObjectUtil;
import ltd.newbee.mall.common.IndexConfigTypeEnum;
import ltd.newbee.mall.common.NewBeeMallException;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.entity.IndexConfig;
import ltd.newbee.mall.service.NewBeeMallIndexConfigService;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/admin")
public class NewBeeMallGoodsIndexConfigController {


    @Resource
    private NewBeeMallIndexConfigService indexConfigService;


    @GetMapping("/indexConfigs")
    public String indexConfigPage(HttpServletRequest request, @RequestParam("configType") int configType) {

        IndexConfigTypeEnum type = IndexConfigTypeEnum.getIndexConfigTypeEnumByType(configType);

        if (type.equals(IndexConfigTypeEnum.DEFAULT)) NewBeeMallException.fail("参数异常");

        request.setAttribute("path",type.getName());
        request.setAttribute("configType",configType);

        return "admin/newbee_mall_index_config";

    }

    /**
     * 列表
     * @param params
     * @return
     */
    @GetMapping("/indexConfigs/list")
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {

        if (!StringUtils.hasText((String) params.get("page"))
                || !StringUtils.hasText((String) params.get("limit")))
            return ResultGenerator.genFailResult("参数异常!");

        PageQueryUtil pqu = new PageQueryUtil(params);

        return ResultGenerator.genSuccessResult(indexConfigService.getConfigsPage(pqu));

    }

    /**
     * 添加
     * @param indexConfig
     * @return
     */
    @PostMapping("/indexConfigs/save")
    @ResponseBody
    public Result save(@RequestBody IndexConfig indexConfig){

        if (ObjectUtils.isEmpty(indexConfig.getConfigType()) ||
        !StringUtils.hasText(indexConfig.getConfigName()) ||
                ObjectUtil.isNull(indexConfig.getConfigRank())) return ResultGenerator.genFailResult("参数异常!");

        String result = indexConfigService.saveIndexConfig(indexConfig);

        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) return ResultGenerator.genSuccessResult(result);

        return ResultGenerator.genFailResult(result);
    }

    /**
     * update 修改
     * @param indexConfig
     * @return
     */
    @PostMapping("/indexConfigs/update")
    @ResponseBody
    public Result update(@RequestBody IndexConfig indexConfig){

        if (Objects.isNull(indexConfig.getConfigType()) ||
        Objects.isNull(indexConfig.getConfigId()) ||
        !StringUtils.hasText(indexConfig.getConfigName()) ||
        Objects.isNull(indexConfig.getConfigRank())) return ResultGenerator.genFailResult("参数异常!");

        String result = indexConfigService.updateIndexConfig(indexConfig);

        if (ServiceResultEnum.SUCCESS.getResult()
                .equals(result)) return ResultGenerator.genSuccessResult(result);

        return ResultGenerator.genFailResult(result);
    }

    /**
     * 详情 info
     * @param id
     * @return
     */
    @GetMapping("/indexConfigs/info/{id}")
    @ResponseBody
    public Result info(@PathVariable(name = "id") Long id) {

        IndexConfig indexConfig = indexConfigService.getIndexConfigById(id);

        if (indexConfig == null) return ResultGenerator.genFailResult("未查询到数据!");

        return ResultGenerator.genSuccessResult(indexConfig);

    }


    @PostMapping("/indexConfigs/delete")
    @ResponseBody
    public Result delete(@RequestBody Long[] ids) {

        if (ids.length < 1) return ResultGenerator.genFailResult("参数异常!");

        if (indexConfigService.deleteBatch(ids)) return ResultGenerator.genSuccessResult();

        return ResultGenerator.genFailResult("删除失败!");

    }


}
