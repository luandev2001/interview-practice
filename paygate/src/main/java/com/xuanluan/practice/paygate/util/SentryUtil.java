package com.xuanluan.practice.paygate.util;

import io.sentry.IScope;
import io.sentry.Sentry;
import jakarta.servlet.http.HttpServletRequest;

public class SentryUtil {
    public static void captureException(Throwable throwable, HttpServletRequest request) {
        Sentry.configureScope(scope -> enrichScopeWithRequest(scope, request));
        Sentry.captureException(throwable);


    }

    private static void enrichScopeWithRequest(IScope scope, HttpServletRequest request) {
        scope.setTag("url", request.getRequestURL().toString());
        scope.setTag("method", request.getMethod());

        request.getParameterMap().forEach((key, value) -> {
            scope.setExtra("param_" + key, String.join(",", value));
        });

        var headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            scope.setExtra("header_" + header, request.getHeader(header));
        }

        scope.setExtra("ip_address", request.getRemoteAddr());
    }
}
