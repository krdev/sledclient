/**
 *
 */
package com.lafaspot.pop.command;

import javax.annotation.Nonnull;


/**
 * Object representing response from server.
 *
 * @author kraman
 *
 */
public class PopResponse {

    private final String command;

    private final String args[];

    /**
     * Constructor to create PopResponse.
     *
     * @param command the command string
     * @param args arguments
     */
    public PopResponse(@Nonnull final String command, @Nonnull final String[] args) {
        this.command = command;
        this.args = args;
    }

    /**
     * @return the command
     */
    public String getCommand() {
        return command;
    }

    /**
     * @return the args
     */
    public String[] getArgs() {
        return args;
    }

}
