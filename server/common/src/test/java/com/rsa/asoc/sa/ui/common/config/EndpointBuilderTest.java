package com.rsa.asoc.sa.ui.common.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests {@link EndpointBuilder}
 *
 * @author nguyek7
 * @since 10.6.0
 */
public class EndpointBuilderTest {

    @Test
    public void testNoSlash() {
        final EndpointBuilder endpoint = new EndpointBuilder("test");
        String path = endpoint.buildWebSocketPath();
        assertNotNull(path);
        assertEquals("/test/socket", path);
    }

    @Test
    public void testLeadingSlash() {
        final EndpointBuilder endpoint = new EndpointBuilder("/test");
        String path = endpoint.buildWebSocketPath();
        assertNotNull(path);
        assertEquals("/test/socket", path);
    }

    @Test
    public void testTrailingSlash() {
        final EndpointBuilder endpoint = new EndpointBuilder("test/");
        String path = endpoint.buildWebSocketPath();
        assertNotNull(path);
        assertEquals("/test/socket", path);
    }

    @Test
    public void testSurroundingSlash() {
        final EndpointBuilder endpoint = new EndpointBuilder("/test/");
        String path = endpoint.buildWebSocketPath();
        assertNotNull(path);
        assertEquals("/test/socket", path);
    }

    @Test
    public void anyWebSocketRequestAntMatcher() {
        final EndpointBuilder endpoint = new EndpointBuilder("/test");
        String matcher = endpoint.anyWebSocketRequestAntMatcher();
        assertNotNull(matcher);
        assertEquals("/test/socket/**", matcher);
    }
}
