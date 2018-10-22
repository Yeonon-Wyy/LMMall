package top.yeonon.serivice;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Author yeonon
 * @date 2018/4/6 0006 17:54
 **/
public interface IFileService {
    String upload(MultipartFile file, String path);
}
