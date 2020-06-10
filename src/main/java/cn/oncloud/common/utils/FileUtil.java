package cn.oncloud.common.utils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class FileUtil {

    public static void download(String filename, HttpServletResponse res) throws IOException {
        // 发送给客户端的数据
        OutputStream outputStream = res.getOutputStream();
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        // 读取filename
        bis = new BufferedInputStream(new FileInputStream(new File("./FileLibrary/" + filename)));
        int i = bis.read(buff);
        while (i != -1) {
            outputStream.write(buff, 0, buff.length);
            outputStream.flush();
            i = bis.read(buff);
        }
    }

    //获取本项目路径
    public static String systemPath() {
        File file = new File("FileLibrary");
        // E:\IdeaProjects\filesystem\FileLibrary
        return file.getAbsolutePath();
    }
}

