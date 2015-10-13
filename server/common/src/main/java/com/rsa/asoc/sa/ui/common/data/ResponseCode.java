package com.rsa.asoc.sa.ui.common.data;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Return codes that are used in the {@link Response}.  The client-side will convert the codes
 * into meaningful, translated errors.
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
public enum ResponseCode {
    /**
     * A successful response
     */
    SUCCESS(0),

    /**
     * Any exception that doesn't match a more specific exception
     */
    GENERAL_EXCEPTION(1);

    private final int code;

    ResponseCode(int code) {
        this.code = code;
    }

    @JsonValue
    public int getCode() {
        return code;
    }
}
