package ltd.newbee.mall.controller.common;

import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.util.NewBeeMallUtils;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/admin")
public class UploadController {

    @Autowired
    private StandardServletMultipartResolver standardServletMultipartResolver;

    /**
     * 文件上传图片
     * @param request
     * @param file
     * @return
     */
    @PostMapping("/upload/file")
    public Result upload(HttpServletRequest request, @RequestParam MultipartFile file) {

        try {
            String originalFilename = file.getOriginalFilename();

            BufferedImage read = ImageIO.read(file.getInputStream());

            if (read == null) {
                return ResultGenerator.genFailResult("请上传图片类型的文件");
            }

            String suffixName = originalFilename.substring(originalFilename.lastIndexOf("."));

            //生成新文件名称
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            Random ran = new Random();
            StringBuilder sb = new StringBuilder();
            sb.append(sdf.format(new Date())).append(ran.nextInt(100)).append(suffixName);
            String newName = sb.toString();

            File fileDir = new File(Constants.FILE_UPLOAD_DIC);
            File newFile = new File(Constants.FILE_UPLOAD_DIC + newName);

            if (!fileDir.exists()) {
                if (!fileDir.mkdir()) {
                    throw new IOException("文件创建失败,文件地址:"+fileDir);
                }
            }

            file.transferTo(newFile);
            Result result = ResultGenerator.genSuccessResult();
            result.setData(NewBeeMallUtils.getHost(new URI(request.getRequestURI()+""))+"/upload/"+newName);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return ResultGenerator.genFailResult("文件上传失败");
            //throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return ResultGenerator.genFailResult("文件上传失败");
            //throw new RuntimeException(e);
        }

    }

    @PostMapping("/upload/files")
    public Result uploadV2(HttpServletRequest request) {

        List<MultipartFile> multipartFiles = new ArrayList<>(8);

        List<String> fileNames = null;
        try {
            if (standardServletMultipartResolver.isMultipart(request)) {

                MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;

                Iterator<String> names = multiRequest.getFileNames();

                //筛选文件
                int total = 0;
                while (names.hasNext()) {
                    if (total > 5) {
                        return ResultGenerator.genFailResult("最多上传五张图片");
                    }
                    total++;
                    MultipartFile file = multiRequest.getFile(names.next());
                    BufferedImage read = ImageIO.read(file.getInputStream());
                    if (read != null) {
                        multipartFiles.add(file);
                    }
                }
            }
            if (CollectionUtils.isEmpty(multipartFiles)) {
                return ResultGenerator.genFailResult("请上传图片类型的文件");
            }

            if (multipartFiles != null && multipartFiles.size() > 5) {
                return ResultGenerator.genFailResult("最多上传五张图片");
            }

            File fileDir = new File(Constants.FILE_UPLOAD_DIC);
            if (!fileDir.exists()) {
                if (!fileDir.mkdir()) {
                    return ResultGenerator.genFailResult("文件夹创建失败");
                }
            }
            fileNames = new ArrayList<>();
            List<String> finalFileNames = fileNames;
            multipartFiles.forEach(file -> {

                String suffixName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));

                //新文件名
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                Random ran = new Random();
                StringBuilder sb = new StringBuilder();
                sb.append(sdf.format(new Date())).append(ran.nextInt(100)).append(suffixName);
                String newName = sb.toString();

                File newFile = new File(Constants.FILE_UPLOAD_DIC + newName);

                try {
                    file.transferTo(newFile);

                    finalFileNames.add(NewBeeMallUtils.getHost(new URI(request.getRequestURI()))+ "/upload/" + newName);
                } catch (IOException e) {
                    e.printStackTrace();
                    //throw new RuntimeException(e);
                } catch (URISyntaxException e) {
                    //throw new RuntimeException(e);
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
            return ResultGenerator.genFailResult("文件上传失败");
            //throw new RuntimeException(e);
        }

        return ResultGenerator.genSuccessResult(fileNames);

    }
}
