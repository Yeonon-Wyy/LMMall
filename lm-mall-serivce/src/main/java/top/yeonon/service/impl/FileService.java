package top.yeonon.service.impl;


import com.google.common.collect.Lists;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.yeonon.service.IFileService;
import top.yeonon.util.FTPUtil;


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

    //上传文件
    @Override
    public String upload(MultipartFile file, String path) {
        String fileName = file.getOriginalFilename();
        //获取扩展名，为了构造一个随机的文件名并且不丢失扩展名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        //构造随机文件名，防止重复
        String targetFileName = UUID.randomUUID().toString() + "." + fileExtensionName;

        log.info("开始上传文件 " + fileName + " 上传路径是 : " + path);

        //打开文件夹
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            //不存在文件夹，我们就创建他
            fileDir.setWritable(true);
            fileDir.mkdir();
        }

        //构造文件在文件夹目录下
        File targetFile = new File(path, targetFileName);

        try {
            file.transferTo(targetFile);

            //传到FTP服务器
            FTPUtil.uploadFiles(Lists.newArrayList(targetFile));

            targetFile.delete();

        } catch (IOException e) {
            log.info("上传文件失败");
        }
        return targetFile.getName();
    }
}
