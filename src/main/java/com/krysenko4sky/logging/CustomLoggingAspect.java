package com.krysenko4sky.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Conditional(LogLevelDebugCondition.class)
public class CustomLoggingAspect {

    @Pointcut("execution(public * com.krysenko4sky.service..*(..)) || execution(public * com.krysenko4sky.controller..*(..))")
    public void publicMethodInServiceOrController() {
    }

    @After("publicMethodInServiceOrController()")
    public void logAfter(JoinPoint joinPoint) {
        Logger logger = getLogger(joinPoint);
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        if (!joinPoint.getTarget().getClass().isAnnotationPresent(LogArguments.class)) {
            args = new String[]{"*******"};
        }
        logger.debug("Method {} called with arguments: {}", methodName, args);
    }

    @AfterReturning(pointcut = "publicMethodInServiceOrController()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        Logger logger = getLogger(joinPoint);
        String methodName = joinPoint.getSignature().getName();
        logger.debug("Method {} completed successfully with result: {}", methodName, result);
    }

    @AfterThrowing(pointcut = "publicMethodInServiceOrController()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        Logger logger = getLogger(joinPoint);
        String methodName = joinPoint.getSignature().getName();
        logger.error("Method {} threw an exception: {}", methodName, ex.getMessage(), ex);
    }

    private static Logger getLogger(JoinPoint joinPoint) {
        return LoggerFactory.getLogger(joinPoint.getTarget().getClass());
    }
}
