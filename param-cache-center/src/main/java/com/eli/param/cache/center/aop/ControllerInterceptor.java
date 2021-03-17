package com.eli.param.cache.center.aop;

import com.eli.param.cache.center.utils.result.BaseResult;
import com.eli.param.cache.center.utils.result.BizEnum;
import com.eli.param.cache.center.utils.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * @author eli
 */
@Slf4j
@Aspect
@Component
public class ControllerInterceptor {

    @Pointcut(value = "(@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping)) " +
            "&& (execution(com.eli.param.cache.center.utils.result.Result *.*(..))))")
    public void pointcut() {
    }


    @Around("pointcut()")
    public BaseResult intercept(ProceedingJoinPoint joinPoint) throws Throwable {
        BaseResult result;
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            result = (BaseResult) joinPoint.proceed();
            watch.stop();
            String info = joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()";
            log.info("Method==>{}, execute Time==>{}ms", info, watch.getTotalTimeMillis());
        } catch (Exception e) {
            watch.stop();
            String info = joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()";
            log.error("[异常] Method==>{}, execute Time==>{}ms", info, watch.getTotalTimeMillis(), e);
            result = Result.wrapErrorResult(BizEnum.ERROR);
        }
        return result;
    }


}
