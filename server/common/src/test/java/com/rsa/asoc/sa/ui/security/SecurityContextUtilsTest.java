package com.rsa.asoc.sa.ui.security;

import org.junit.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.concurrent.Callable;

import static org.junit.Assert.*;

/**
 * Tests for {@link SecurityContextUtils}
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
public class SecurityContextUtilsTest {

    @Test
    public void testWrapRunnable() throws Exception {
        Authentication authentication = new TestingAuthenticationToken("admin", "netwitness", "ADMIN");

        SecurityContext context = SecurityContextHolder.getContext();
        assertNotNull(context);
        assertNull(context.getAuthentication());

        Runnable wrapped = SecurityContextUtils.wrap(authentication, () -> {
            SecurityContext innerContext = SecurityContextHolder.getContext();

            assertNotNull(innerContext);
            assertEquals(authentication, innerContext.getAuthentication());
        });

        wrapped.run();

        context = SecurityContextHolder.getContext();
        assertNotNull(context);
        assertNull(context.getAuthentication());
    }

    @Test
    public void testWrapRunnableThatThrows() throws Exception {
        Authentication authentication = new TestingAuthenticationToken("admin", "netwitness", "ADMIN");

        SecurityContext context = SecurityContextHolder.getContext();
        assertNotNull(context);
        assertNull(context.getAuthentication());

        Runnable wrapped = SecurityContextUtils.wrap(authentication, (Runnable) () -> {
            SecurityContext innerContext = SecurityContextHolder.getContext();

            assertNotNull(innerContext);
            assertEquals(authentication, innerContext.getAuthentication());

            throw new RuntimeException("failed");
        });

        try {
            wrapped.run();
            fail("should have thrown");
        }
        catch (Exception ignore) {
            // swallow
        }

        context = SecurityContextHolder.getContext();
        assertNotNull(context);
        assertNull(context.getAuthentication());
    }

    @Test
    public void testWrapCallable() throws Exception {
        Authentication authentication = new TestingAuthenticationToken("admin", "netwitness", "ADMIN");

        SecurityContext context = SecurityContextHolder.getContext();
        assertNotNull(context);
        assertNull(context.getAuthentication());

        Callable<String> wrapped = SecurityContextUtils.wrap(authentication, () -> {
            SecurityContext innerContext = SecurityContextHolder.getContext();

            assertNotNull(innerContext);
            assertEquals(authentication, innerContext.getAuthentication());

            return authentication.getName();
        });

        String name = wrapped.call();
        assertEquals("admin", name);

        context = SecurityContextHolder.getContext();
        assertNotNull(context);
        assertNull(context.getAuthentication());
    }

    @Test
    public void testWrapCallableThatThrows() throws Exception {
        Authentication authentication = new TestingAuthenticationToken("admin", "netwitness", "ADMIN");

        SecurityContext context = SecurityContextHolder.getContext();
        assertNotNull(context);
        assertNull(context.getAuthentication());

        Callable<String> wrapped = SecurityContextUtils.wrap(authentication, () -> {
            SecurityContext innerContext = SecurityContextHolder.getContext();

            assertNotNull(innerContext);
            assertEquals(authentication, innerContext.getAuthentication());

            throw new RuntimeException("failed");
        });

        try {
            wrapped.call();
            fail("should have thrown");
        }
        catch (Exception ignore) {
            // swallow
        }

        context = SecurityContextHolder.getContext();
        assertNotNull(context);
        assertNull(context.getAuthentication());
    }
}
