package com.krysenko4sky.logging;

import ch.qos.logback.classic.Level;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Objects;

public class LogLevelDebugCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return Objects.equals(
                context.getEnvironment().getProperty("logging.level.com.krysenko4sky"),
                Level.DEBUG.levelStr);
    }
}
