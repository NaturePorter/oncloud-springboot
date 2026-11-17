package cn.oncloud.common.utils;

import cn.oncloud.exception.Campuso2oException;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 文件下载工具类
 */
public class DownloadFileUtil {

    private DownloadFileUtil() {
    }

    /**
     * 把文件写入响应流
     *
     * @param originalFileName 下载时展示的文件名
     * @param file             磁盘上的真实文件
     * @param response         响应对象
     * @param downloadfrequency 当前下载次数
     * @return 下载完成后的次数
     */
    public static Long downloadFile(String originalFileName, File file, HttpServletResponse response, Long downloadfrequency) {
        checkParam(originalFileName, file);
        response.reset();
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setContentLengthLong(file.length());
        response.setHeader("Content-Disposition", buildContentDisposition(originalFileName));

        try (InputStream in = new FileInputStream(file);
             ServletOutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            out.flush();
        } catch (IOException e) {
            throw new Campuso2oException("文件下载失败：" + e.getMessage());
        }
        return downloadfrequency == null ? 1L : downloadfrequency + 1;
    }

    /**
     * 同时兼容现代浏览器（filename*）与旧浏览器（filename）
     */
    private static String buildContentDisposition(String originalFileName) {
        String encoded = URLEncoder.encode(originalFileName, StandardCharsets.UTF_8).replace("+", "%20");
        return "attachment; filename=\"" + encoded + "\"; filename*=UTF-8''" + encoded;
    }

    private static void checkParam(String originalFileName, File file) {
        if (StringUtils.isBlank(originalFileName)) {
            throw new Campuso2oException("下载文件名不能为空");
        }
        if (file == null || !file.exists() || !file.isFile()) {
            throw new Campuso2oException("待下载的文件不存在");
        }
    }
}
