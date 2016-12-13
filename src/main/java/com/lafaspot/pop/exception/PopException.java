/**
 *
 */
package com.lafaspot.pop.exception;

import javax.annotation.Nonnull;
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

    public enum Type {
        CONNECT_FAILURE, TIMEDOUT
    }

}
