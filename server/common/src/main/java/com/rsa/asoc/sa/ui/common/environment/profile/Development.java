package com.rsa.asoc.sa.ui.common.environment.profile;

import org.springframework.context.annotation.Profile;

/**
 * Indicates that a component is available only when the 'development' Spring profile is active. See {@link Profile}.
 *
 * @author Jay Garala
 * @since 10.6.0
 */
@Profile("development")
public @interface Development {
}
