/*
package cn.oncloud.filter;

import cn.oncloud.dto.base.ResultBean;
import cn.oncloud.dto.base.ResultConst;
import cn.oncloud.util.JwtUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

*/
/**
 * @author 余弘洋
 * 功能描述：拦截器，拦截前端请求，验证其中的token令牌是否合法
 *//*

public class AuthorizeFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("我出生了！");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (request.getMethod().equals(RequestMethod.OPTIONS.name())) {
            response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
            response.setHeader("Access-Control-Allow-Methods", "PUT, DELETE, GET, POST");
            response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpStatus.OK.value());
            return;
        }

        //3.判断当前的请求是否为登录请求,如果是,则直接放行
        if (request.getRequestURI().contains("/login")){
            chain.doFilter(request, response);
        */
/**判断当前请求是否为下载，如果是，就放行，有安全隐患，后期改正*//*

        } else if(request.getRequestURI().contains("/download")){
            chain.doFilter(request, response);
        } else {
            //4.获取当前的所有请求头信息
            String jwtToken = request.getHeader("token");
            //6.判断当前令牌是否存在, 如果不存在,则向客户端返回错误提示信息
            if (StringUtils.isEmpty(jwtToken)) {
                ResultBean error = ResultBean.error(HttpStatus.FORBIDDEN.value(), ResultConst.INVALID_TOKEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().print(JSONObject.toJSON(error));
                return;
            }

            //6.1 如果令牌存在,解析jwt令牌,判断该令牌是否合法,如果令牌不合法,则向客户端返回错误提示信息
            try {
                JwtUtil.parseJWT(jwtToken);
            } catch (Exception e) {
                e.printStackTrace();
                ResultBean error = ResultBean.error(HttpStatus.FORBIDDEN.value(), ResultConst.INVALID_TOKEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().print(JSONObject.toJSON(error));
                return;
            }

            //6.2 如果令牌合法,则放行
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        System.out.println("我死了！");
    }
}
*/
