package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.config.UploadProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
@Slf4j
@EnableConfigurationProperties(UploadProperties.class)
public class UploadService {

    /**
     * 设置允许上传的格式
     */
    //private static final List<String> allowTypes = Arrays.asList("image/jpeg","image/png","image/bmp");

    @Autowired
    private UploadProperties uploadProperties;

    @Autowired
    private FastFileStorageClient client;

    public String uploadImage(MultipartFile file) {
        try {
            // 文件格式校验
            String contentType = file.getContentType();
            if (!uploadProperties.getAllowTypes().contains(contentType)) {
                throw new LyException(ExceptionEnum.UPLOAD_TYPE_ERROR);
            }
            // 校验图片内容
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new LyException(ExceptionEnum.UPLOAD_TYPE_ERROR);
            }
            // 准备目标路径
            //File dest = new File("D:\\data\\"+file.getOriginalFilename());
            // 上传到
            //file.transferTo(dest);
            // 返回路径
            // 获取后缀
            String extension = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
            StorePath storePath = client.uploadFile(file.getInputStream(), file.getSize(), extension, null);
            return uploadProperties.getBaseUrl() + storePath.getFullPath();
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new LyException(ExceptionEnum.UPLOAD_ERROR);
        }
    }
}
