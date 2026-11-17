package cn.oncloud.filter.oauth2;

import cn.oncloud.common.utils.HttpContextUtils;
import cn.oncloud.dto.base.ResultBean;
import com.alibaba.fastjson2.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * oauth2过滤器
 *
 * @author YYYY
 */
public class OAuth2Filter extends AuthenticatingFilter {

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        String token = getRequestToken((HttpServletRequest) request);
        if (StringUtils.isBlank(token)) {
            return null;
        }
        return new OAuth2Token(token);
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        // 浏览器跨域预检请求直接放行
        return RequestMethod.OPTIONS.name().equals(((HttpServletRequest) request).getMethod());
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        String token = getRequestToken((HttpServletRequest) request);
        if (StringUtils.isBlank(token)) {
            writeError(response, "token失效，请重新登录");
            return false;
        }
        return executeLogin(request, response);
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e,
                                     ServletRequest request, ServletResponse response) {
        Throwable throwable = e.getCause() == null ? e : e.getCause();
        writeError(response, throwable.getMessage());
        return false;
    }

    private void writeError(ServletResponse response, String message) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        httpResponse.setContentType("application/json;charset=utf-8");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpResponse.setHeader("Access-Control-Allow-Origin", HttpContextUtils.getOrigin());
        try {
            String json = JSON.toJSONString(ResultBean.error(HttpStatus.UNAUTHORIZED.value(), message));
            httpResponse.getWriter().print(json);
        } catch (IOException ignored) {
            // 客户端已断开时无需处理
        }
    }

    /**
     * 获取请求的 token：优先 header，其次 query 参数（下载链接场景）
     */
    private String getRequestToken(HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("token");
        if (StringUtils.isBlank(token)) {
            token = httpRequest.getParameter("token");
        }
        return token;
    }
}
