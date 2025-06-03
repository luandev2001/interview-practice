package com.xuanluan.practice.interviewnonblocking.constant;

import java.util.regex.Pattern;

public class RequestConstant {
    public static class Body {
        public static final Pattern SENSITIVE_AND_SPACE_PATTERN = Pattern.compile("\"(password|secret|token)\"\\s*:\\s*\"[^\"]*\"|\\s+");
    }
}
