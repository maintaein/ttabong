package com.ttabong.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateTimeUtil {

    private static final String DEFAULT_ZONE = "Asia/Seoul"; // 기본 타임존 설정

    public static LocalDateTime convertToLocalDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.atZone(ZoneId.of(DEFAULT_ZONE)).toLocalDateTime();
    }
}