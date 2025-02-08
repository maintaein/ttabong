package com.ttabong.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface LoggerConfig {
    default Logger logger() {
        return LoggerFactory.getLogger(this.getClass());
    }
}
