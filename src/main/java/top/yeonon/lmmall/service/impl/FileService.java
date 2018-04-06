package top.yeonon.lmmall.service.impl;

import com.google.common.collect.Lists;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.service.IFileService;
import top.yeonon.lmmall.utils.FTPUtil;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @Author yeonon
 * @date 2018/4/6 0006 17:55
 **/
@Service
@Log
public class FileService implements IFileService {

    @Override
    public String upload(MultipartFile file, String path) {
        String fileName = file.getOriginalFilename();
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;

        log.info("开始上传文件 ： " + fileName + " 上传路径是 ： " + path);

        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdir();
        }

        File targetFile = new File(path, uploadFileName);

        try {
            file.transferTo(targetFile);

            //FTP上传

            FTPUtil.uploadFiles(Lists.newArrayList(targetFile));

            targetFile.delete();
        } catch (IOException e) {
            log.info("上传文件失败");
        }
        return targetFile.getName();
    }
}
