package com.rsa.asoc.sa.ui.common.config;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * The interface to build Endpoint related information.
 */
public class EndpointBuilder {

    private String prefix;

    public EndpointBuilder(String prefix) {
        setPrefix(prefix);
    }

    /**
     * Ensures the provided prefix is valid and creates the fully-qualified STOMP endpoint path in the form
     * of: [prefix]/socket
     * @return the fully-qualified STOMP endpoint path
     */
    public String buildWebSocketPath() {
        Preconditions.checkState(!Strings.isNullOrEmpty(prefix), "Endpoint prefix must be defined.");

        StringBuilder builder = new StringBuilder(prefix);
        if (!prefix.startsWith("/")) {
            builder.insert(0, "/");
        }
        if (!prefix.endsWith("/")) {
            builder.append("/");
        }

        builder.append("socket");

        return builder.toString();
    }

    /**
     * Build a Matcher to match requests for the Web Socket path.
     * @param antPatten The pattern after Web Socket path
     * @return The Ant Matcher as String
     */
    public String buildWebSocketAntMatcher(String antPatten) {
        StringBuilder result = new StringBuilder(buildWebSocketPath());
        if (!antPatten.startsWith("/")) {
            result.append("/");
        }
        result.append(antPatten);
        return result.toString();
    }

    /**
     * Build a Matcher to match all requests for the Web Socket path.
     * @return The Ant Matcher as String
     */
    public String anyWebSocketRequestAntMatcher() {
        return buildWebSocketAntMatcher("/**");
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
