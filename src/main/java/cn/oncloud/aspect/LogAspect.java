package cn.oncloud.aspect;

import java.lang.reflect.Method;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 交给spring容器管理
 * @author 余弘洋
 */
@Component
/**
 * 标识这个是一个切面
 */
@Aspect
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 以自定义@WebLog注解为切点
     */
    @Pointcut("@annotation(cn.oncloud.aspect.WebLog)")
    public void webLog() {
    }

    /**
     * 切点之前
     *
     * @param joinPoint
     * @throws Throwable
     */
    @Before("webLog()")
    public void before(JoinPoint joinPoint) throws Throwable {
        // 得到 HttpServletRequest
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        logger.info("============ before ==========");
        // 获取WebLog注解信息
        String info = getWebLogInfo(joinPoint);
        logger.info("Point Info    : {}", info);
        // 请求地址URL
        logger.info("请求地址：URL	: {}", request.getRequestURL().toString());
        // 请求方法
        logger.info("请求方式：HTTP Method : {}", request.getMethod());
        // 具体切入执行方法
        logger.info("切入执行方法：Class Method : {}.{}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
        // 请求IP
        logger.info("请求IP：IP	: {}", request.getRemoteAddr());// 打印描述信息
        // 请求参数
        logger.info("请求参数：Input Parameter : {}", Arrays.asList(joinPoint.getArgs()));
    }

    /**
     * 切点之后
     *
     * @throws Throwable
     */
    /*@After("webLog()")
    public void after() throws Throwable {
        logger.info("============ after ==========");
    }*/

    /**
     * 切点返回内容后
     *
     * @throws Throwable
     */
    /*@AfterReturning("webLog()")
    public void afterReturning() throws Throwable {
        logger.info("============ afterReturning ==========");
    }*/

    /**
     * 切点抛出异常后
     *
     * @throws Throwable
     */
    /*@AfterThrowing("webLog()")
    public void afterThrowing() throws Throwable {
        logger.info("============ afterThrowing ==========");
    }*/

    /**
     * 环绕
     *
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        logger.info("============ 环绕开启：doAround ==========");
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        // 打印出参
        logger.info("返回参数：Output Parameter : {}", result);
        // 执行时间
        logger.info("执行时间：Execution Time : {} ms", System.currentTimeMillis() - startTime);
        return result;
    }

    /**
     * 获取web日志注解信息
     *
     * @param joinPoint
     * @return
     * @throws Exception
     */
    public String getWebLogInfo(JoinPoint joinPoint) throws Exception {
        // 获取切入点的目标类
        String targetName = joinPoint.getTarget().getClass().getName();
        Class<?> targetClass = Class.forName(targetName);
        // 获取切入方法名
        String methodName = joinPoint.getSignature().getName();
        // 获取切入方法参数
        Object[] arguments = joinPoint.getArgs();
        // 获取目标类的所有方法
        Method[] methods = targetClass.getMethods();
        for (Method method : methods) {
            // 方法名相同、包含目标注解、方法参数个数相同（避免有重载）
            if (method.getName().equals(methodName) && method.isAnnotationPresent(WebLog.class)
                    && method.getParameterTypes().length == arguments.length) {
                return method.getAnnotation(WebLog.class).value();
            }
        }
        return "";
    }

}
