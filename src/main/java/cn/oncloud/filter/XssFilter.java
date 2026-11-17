package cn.oncloud.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * XSS 防护过滤器
 */
public class XssFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        // 登录密码允许包含特殊字符，不做转义
        if (httpRequest.getRequestURI().endsWith("/api/login")) {
            chain.doFilter(request, response);
            return;
        }
        chain.doFilter(new XSSRequestWrapper(httpRequest), response);
    }

    @Override
    public void destroy() {
    }
}
