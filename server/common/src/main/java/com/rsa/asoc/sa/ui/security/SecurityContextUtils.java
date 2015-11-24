package com.rsa.asoc.sa.ui.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.concurrent.Callable;

/**
 * A helper utilities to manipulate the current {@link SecurityContext}.  This is mostly used in asynchronous
 * threads that are not managed by Spring.
 *
 * @author Abram Thielke
 * @since 0.0
 */
public final class SecurityContextUtils {

    private SecurityContextUtils() {
    }

    /**
     * Returns a new {@link Runnable} that sets the {@link SecurityContext} to the given {@link Authentication}
     * object, before calling the passed-in {@link Runnable}.
     *
     * The current {@link SecurityContext} will be restored afterwards.
     */
    public static Runnable wrap(Authentication authentication, Runnable runnable) {
        return () -> {
            SecurityContext currentSecurityContext = SecurityContextHolder.getContext();
            try {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);

                runnable.run();
            }
            finally {
                SecurityContextHolder.setContext(currentSecurityContext);
            }
        };
    }

    /**
     * Returns a new {@link Callable} that sets the {@link SecurityContext} to the given {@link Authentication}
     * object, before calling the passed-in {@link Callable}.
     *
     * The current {@link SecurityContext} will be restored afterwards.
     */
    public static <T> Callable<T> wrap(Authentication authentication, Callable<T> callable) throws Exception {
        return () -> {
            SecurityContext currentSecurityContext = SecurityContextHolder.getContext();
            try {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);

                return callable.call();
            }
            finally {
                SecurityContextHolder.setContext(currentSecurityContext);
            }
        };
    }
}
