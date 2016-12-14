/**
 *
 */
package com.lafaspot.pop.exception;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
/**
 * @author kraman
 *
 */
public class PopException extends Exception {

    private String message = null;

    private Type type;

    public PopException(@Nonnull Type type) {
        super(type.toString());
        this.type = type;
    }

    public PopException(@Nonnull final Type failureType, @Nullable final Throwable cause) {
        super(failureType.toString(), cause);
    }

    public enum Type {
        CONNECT_FAILURE, TIMEDOUT, PARSE_FAILURE, INVALID_STATE
    }

}
