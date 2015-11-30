package com.rsa.asoc.sa.ui.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.eclipse.jetty.server.Server;

/**
 * Security Settings
 *
 * @author Jay Garala
 * @since 10.6.0
 */
@ConfigurationProperties(prefix = "security")
public class SecuritySettings {

    private Authentication auth = new Authentication();

    public Authentication getAuth() {
        return auth;
    }

    public void setAuth(Authentication auth) {
        this.auth = auth;
    }

    /**
     * Authentication Settings
     */
    public static class Authentication {


        public Server server = new Server();

        public Server getServer() {
            return server;
        }

        public void setServer(Server server) {
            this.server = server;
        }

        /**
         * Authentication Service Settings
         */
        public static class Server {
            private String url = "https://localhost/api/auth/login";

            private boolean allowInsecureSsl = false;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public boolean isAllowInsecureSsl() {
                return allowInsecureSsl;
            }

            public void setAllowInsecureSsl(boolean allowInsecureSsl) {
                this.allowInsecureSsl = allowInsecureSsl;
            }
        }
    }
}
