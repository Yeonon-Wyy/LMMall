package top.yeonon.lmmall.utils;

import lombok.extern.java.Log;
import org.apache.commons.net.ftp.FTPClient;
import top.yeonon.lmmall.properties.CoreProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @Author yeonon
 * @date 2018/4/6 0006 18:13
 **/
@Log
public class FTPUtil {

    private static CoreProperties coreProperties;

    static {
        coreProperties = new CoreProperties();
    }

    private final static String ip = coreProperties.getFtp().getIp();
    private final static String username = coreProperties.getFtp().getUsername();
    private final static String password = coreProperties.getFtp().getPassword();

    private static FTPClient ftpClient;



    public static boolean uploadFiles(List<File> files) throws IOException {
        log.info("开始上传文件");
        boolean isSuccess = uploadFiles("img/", files);
        log.info("上传文件到FTP服务器成功");
        return isSuccess;
    }

    private static boolean uploadFiles(String remotePath, List<File> files) throws IOException {
        boolean isSuccess = true;
        FileInputStream fileInputStream = null;
        if (connectFtpServer()) {
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();

                for (File file : files) {
                    fileInputStream = new FileInputStream(file);
                    ftpClient.storeFile(file.getName(), fileInputStream);
                }
            } catch (IOException e) {
                isSuccess = false;
                log.info("上传文件异常");
            } finally {
                ftpClient.disconnect();
                fileInputStream.close();
            }
        }
        return isSuccess;
    }

    private static boolean connectFtpServer() {
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(username, password);
        } catch (IOException e) {
            isSuccess = false;
            log.info("连接FTP服务器异常");
        }
        return isSuccess;
    }


}
