package top.yeonon.common.util;

import lombok.extern.java.Log;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import top.yeonon.common.properties.CoreProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @Author yeonon
 * @date 2018/4/7 0007 15:25
 **/
@Log
public class FTPUtil {

    private final static CoreProperties coreProperties;
    private final static FTPClient ftpClient;


    static {
        coreProperties = new CoreProperties();
        ftpClient = new FTPClient();
    }

    private final static String ip = coreProperties.getFtp().getIp();
    private final static String username = coreProperties.getFtp().getUsername();
    private final static String password = coreProperties.getFtp().getPassword();


    //上传文件API
    public static boolean uploadFiles(List<File> files) {
        boolean isSuccess = false;
        log.info("开始上传文件到FTP服务器");
        isSuccess = uploadFiles("img/", files);
        log.info("上传文件到FTP服务器状态 " + isSuccess);
        return isSuccess;
    }

    private static boolean uploadFiles(String remotePath, List<File> files) {
        //上传文件之前要先检查ftp服务器是否能够联通
        boolean isSuccess = true;
        InputStream inputStream = null;
        if (isConnect()) {
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.enterLocalPassiveMode();
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

                for (File file : files) {
                    inputStream = new FileInputStream(file);
                    ftpClient.storeFile(file.getName(), inputStream);
                }
            } catch (IOException e) {
                isSuccess = false;
                log.info("上传文件失败");
            }
        }
        return isSuccess;
    }

    private static boolean isConnect() {
        boolean isSuccess = true;
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(username, password);
        } catch (IOException e) {
            isSuccess = false;
            log.info("连接FTP服务器失败，请检查FTP服务器");
        }

        return isSuccess;
    }
}
