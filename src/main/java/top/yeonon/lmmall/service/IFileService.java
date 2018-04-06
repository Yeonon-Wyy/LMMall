package top.yeonon.lmmall.service;

import org.springframework.web.multipart.MultipartFile;
import top.yeonon.lmmall.common.ServerResponse;

/**
 * @Author yeonon
 * @date 2018/4/6 0006 17:54
 **/
public interface IFileService {
    String upload(MultipartFile file, String path);
}
