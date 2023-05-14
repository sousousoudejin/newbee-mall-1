package ltd.newbee.mall.controller;

import ltd.newbee.mall.service.UserService;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;
import ltd.newbee.mall.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/users")
public class PageTestController {

    @Autowired
    private UserService userService;

    // 文件保存路径为在D盘下的upload文件夹，可以按照自己的习惯来修改
    private final static String FILE_UPLOAD_PATH = "D:\\IdeaRepositoty\\project_1\\upload\\";


    @GetMapping("/list")
    public Result list(@RequestParam Map<String, Object> params) {
        Result result = new Result();
        if (!StringUtils.hasText((String) params.get("page")) || !StringUtils.hasText((String) params.get("limit"))) {

            result.setResultCode(500);
            result.setMessage("参数异常!");
            return result;

        }

        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);

        PageResult pageResult = userService.getUserPage(pageQueryUtil);

        result.setResultCode(200);
        result.setMessage("查询成功!");
        result.setData(pageResult.getList());

        return result;

    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file")MultipartFile file) {

        if (file.isEmpty()) {
            return "上传失败";
        }

        String originalFilename = file.getOriginalFilename();
        String suffixName  = originalFilename.substring(originalFilename.lastIndexOf("."));

        //生成文件名称通用方法
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Random r = new Random();
        StringBuilder tempName = new StringBuilder();
        tempName.append(sdf.format(new Date())).append(r.nextInt(100)).append(suffixName);
        String newFileName = tempName.toString();

        try {
            // 保存文件
            byte[] bytes = file.getBytes();
            Path path = Paths.get(FILE_UPLOAD_PATH + newFileName);
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "上传成功";

    }

}
