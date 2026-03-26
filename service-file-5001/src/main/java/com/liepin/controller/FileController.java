package com.liepin.controller;

import com.liepin.MinIOConfig;
import com.liepin.MinIOUtils;
import com.liepin.OSSUtils;
import com.liepin.grace.result.GraceJSONResult;
import com.liepin.grace.result.ResponseStatusEnum;
import com.liepin.pojo.bo.Base64FileBO;
import com.liepin.utils.Base64ToFile;
import com.liepin.grace.result.GraceJSONResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("file")
public class FileController {

    //    www.imooc-hire.com
    public static final String host = "http://192.168.10.9:8000/";

    @GetMapping("hello")
    public Object hello() {
        return "Hello File Service~~~";
    }

    @PostMapping("uploadFace")
    public GraceJSONResult uploadFace1(@RequestParam("file") MultipartFile file,
                                       @RequestParam("userId") String userId,
                                       HttpServletRequest request) throws Exception {

        // 获得文件原始名称
        String filename = file.getOriginalFilename();

//        "abc.123.abc.png"
        // 根据文件名中最后一个点的位置向后进行截取
        String suffixName = filename.substring(filename.lastIndexOf("."));

        // 文件新的名称
        String newFileName = userId + suffixName;

        // 设置文件存储的路径，可以存放在指定的路径中，windows用户需要修改为对应的盘符
        String rootPath = "/users/xingma/desktop/temp" ;//+ File.separator
        // 图片存储的完全路径
        String filePath = rootPath + File.separator + "face" + File.separator + newFileName;

        File newFile = new File(filePath);
        if (!newFile.getParentFile().exists()) {
            // 如果目标文件所在目录不存在，则创建父目录
            // 这里在mac没有权限不会抛出异常，会返回false
            newFile.getParentFile().mkdirs();
        }

        // 将内存中的文件数据写入到磁盘
        file.transferTo(newFile);

        // 生成web可以被访问的url地址  ----  静态映射：StaticResourceConfig
        String userFaceUrl = host + "static/face/" + newFileName;

        return GraceJSONResult.ok(userFaceUrl);
    }

    @Autowired
    private MinIOConfig minIOConfig;

    @PostMapping("uploadFace1")
    public GraceJSONResult uploadFace(@RequestParam("file") MultipartFile file,
                                      @RequestParam("userId") String userId) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        // 获得文件原始名称
        String filename = file.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }

        filename = userId + File.separator + filename;
        MinIOUtils.uploadFile(minIOConfig.getBucketName(), filename, file.getInputStream());

        String imageUrl = minIOConfig.getFileHost()
                + "/"
                + minIOConfig.getBucketName()
                + "/"
                + filename;
        return GraceJSONResult.ok(imageUrl);
    }

    @PostMapping("uploadFace3")
    public GraceJSONResult uploadFace4(@RequestParam("file") MultipartFile file,
                                      @RequestParam("userId") String userId) throws Exception {
        // 获得文件原始名称
        String filename = file.getOriginalFilename();

        filename = userId + File.separator + dealFilename(filename);
        String imageUrl = OSSUtils.uploadFile(file, filename);

        return GraceJSONResult.ok(imageUrl);
    }

    @PostMapping("uploadAdminFace")
    public GraceJSONResult uploadAdminFace(@RequestBody @Valid Base64FileBO base64FileBO) throws Exception {

        String base64 = base64FileBO.getBase64File();

        String suffixName = ".png"; // 后缀
        String uuid = UUID.randomUUID().toString(); // 文件名
        String objectName = uuid + suffixName;  // 对象名

        String rootPath = "/temp" + File.separator;
        String filePath = rootPath
                            + File.separator
                            + "adminFace"
                            + File.separator
                            + objectName;

        Base64ToFile.Base64ToFile(base64, filePath);

        MinIOUtils.uploadFile(minIOConfig.getBucketName(), objectName, filePath);

        String imageUrl = minIOConfig.getFileHost()
                + "/"
                + minIOConfig.getBucketName()
                + "/"
                + objectName;

        return GraceJSONResult.ok(imageUrl);
    }

    /**
     * 上传企业logo
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("uploadLogo")
    public GraceJSONResult uploadLogo(@RequestParam("file") MultipartFile file) throws Exception {

        // 获得文件原始名称
        String filename = file.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }

        filename = "company/logo/" + dealFilename(filename);
        MinIOUtils.uploadFile(minIOConfig.getBucketName(), filename, file.getInputStream());

        String imageUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(),
                filename,
                file.getInputStream(),
                true);
        return GraceJSONResult.ok(imageUrl);
    }

    /**
     * 上传营业职照
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("uploadBizLicense")
    public GraceJSONResult uploadBizLicense(@RequestParam("file") MultipartFile file) throws Exception {

        // 获得文件原始名称
        String filename = file.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }

        filename = "company/bizLicense/" + dealFilename(filename);
        String imageUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(),
                                                filename,
                                                file.getInputStream(),
                                                true);
        return GraceJSONResult.ok(imageUrl);
    }


    @PostMapping("uploadAuthLetter")
    public GraceJSONResult uploadAuthLetter(@RequestParam("file") MultipartFile file) throws Exception {

        // 获得文件原始名称
        String filename = file.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }

        filename = "company/AuthLetter/" + dealFilename(filename);
        String imageUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(),
                filename,
                file.getInputStream(),
                true);
        return GraceJSONResult.ok(imageUrl);
    }

    @PostMapping("uploadPhoto")
    public GraceJSONResult uploadPhoto(
            @RequestParam("files") MultipartFile[] files,
            String companyId) throws Exception {

        if (StringUtils.isBlank(companyId)) companyId = "";

        List<String> fileList = new ArrayList<>();

        for (MultipartFile f : files) {
            // 获得文件原始名称
            String filename = f.getOriginalFilename();

            filename = "company/" + companyId + "/photo/" + dealFilename(filename);
            String imageUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(),
                    filename,
                    f.getInputStream(),
                    true);
            fileList.add(imageUrl);
        }

        return GraceJSONResult.ok(fileList);
    }

    private String dealFilename(String filename) {
        String suffixName = filename.substring(filename.lastIndexOf("."));
        String fName = filename.substring(0, filename.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();
        return fName + "-" + uuid + suffixName;
    }
}
