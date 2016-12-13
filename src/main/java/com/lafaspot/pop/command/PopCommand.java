/**
 *
 */
package com.lafaspot.pop.command;

import javax.annotation.Nonnull;

/**
 * Pop command.
 *
 * @author kraman
 *
 */
public class PopCommand {

    private final String command;

    private final String args[];

    /**
     * Constructor to create PopCommand.
     *
     * @param command the command string
     * @param args arguments
     */
    public PopCommand(@Nonnull final String command, @Nonnull final String[] args) {
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
