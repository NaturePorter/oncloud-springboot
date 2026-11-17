package cn.oncloud.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 交给spring容器管理
 *
 * @author 余弘洋
 */
@Component
@Aspect
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    /**
     * 这些方法的入参包含密码等敏感信息，不打印具体值
     */
    private static final String[] SENSITIVE_METHODS = {"login", "addUser", "adduser"};

    /**
     * 以自定义@WebLog注解为切点
     */
    @Pointcut("@annotation(cn.oncloud.aspect.WebLog)")
    public void webLog() {
    }

    @Before("webLog()")
    public void before(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        logger.info("============ before ==========");
        logger.info("Point Info    : {}", getWebLogInfo(joinPoint));
        logger.info("请求地址：URL	: {}", request.getRequestURL());
        logger.info("请求方式：HTTP Method : {}", request.getMethod());
        logger.info("切入执行方法：Class Method : {}.{}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
        logger.info("请求IP：IP	: {}", request.getRemoteAddr());
        if (!isSensitive(joinPoint)) {
            logger.info("请求参数：Input Parameter : {}", Arrays.asList(joinPoint.getArgs()));
        }
    }

    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        logger.info("返回参数：Output Parameter : {}", result);
        logger.info("执行时间：Execution Time : {} ms", System.currentTimeMillis() - startTime);
        return result;
    }

    private boolean isSensitive(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        for (String sensitive : SENSITIVE_METHODS) {
            if (sensitive.equalsIgnoreCase(methodName)) {
                return true;
            }
        }
        return false;
    }

    private String getWebLogInfo(JoinPoint joinPoint) {
        if (joinPoint.getTarget() == null) {
            return "";
        }
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Method[] methods = joinPoint.getTarget().getClass().getMethods();
        String result = Arrays.stream(methods)
                .filter(method -> method.getName().equals(methodName)
                        && method.isAnnotationPresent(WebLog.class)
                        && method.getParameterTypes().length == arguments.length)
                .map(method -> method.getAnnotation(WebLog.class).value())
                .collect(Collectors.joining(","));
        return result;
    }
}
