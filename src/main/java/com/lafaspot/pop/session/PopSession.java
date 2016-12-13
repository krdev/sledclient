/**
 *
 */
package com.lafaspot.pop.session;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.util.concurrent.Future;

import javax.annotation.Nonnull;

import com.lafaspot.logfast.logging.Logger;
import com.lafaspot.pop.command.PopCommand;
import com.lafaspot.pop.command.PopResponse;
import com.lafaspot.pop.exception.PopException;

/**
 * @author kraman
 *
 */
public class PopSession {

    private int state;

    private final Bootstrap bootstrap;

    @Nonnull
    private final String server;

    private final int port;

    private final Logger logger;

    private Channel sessionChannel;

    public PopSession(@Nonnull Bootstrap bootstrap, @Nonnull final String server, final int port, @Nonnull final Logger logger) {
        this.bootstrap = bootstrap;
        this.server = server;
        this.port = port;
        this.logger = logger;
    }

    public PopFuture<Boolean> connect(final int connectTimeout, final int inactivityTimeout) throws PopException {
        logger.debug(" +++ connect to  " + server, null);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout);
        ChannelFuture future = bootstrap.connect(server, port);
        future.awaitUninterruptibly();

        if (future.isCancelled()) {
            // ignore
        } else if (!future.isSuccess()) {
            throw new PopException(PopException.Type.CONNECT_FAILURE);
        } else {

            this.sessionChannel = future.channel();
            this.sessionChannel.pipeline().addLast("inactivityHandler", new PopInactivityHandler(this, inactivityTimeout, logger));
            // this.sessionChannel.pipeline().addLast(new IcapMessageDecoder(logger));
            // this.sessionChannel.pipeline().addLast(new IcapChannelHandler(this));
        }

        PopFuture<Boolean> f = new PopFuture<Boolean>(future);
        f.done(Boolean.TRUE);
        return f;
    }


    public PopFuture<PopResponse> execute(@Nonnull final PopCommand command) {

        final StringBuilder commandToWrite = new StringBuilder();
        commandToWrite.append(command.getCommand());
        for (String arg : command.getArgs()) {
            commandToWrite.append(arg);
        }
        Future f = sessionChannel.writeAndFlush(commandToWrite);
        PopFuture<PopResponse> ret = new PopFuture<PopResponse>(f);
        return ret;
    }

    /**
     * Callback from netty on channel inactivity.
     */
    public void onTimeout() {
        logger.debug("**channel timeout** TH " + Thread.currentThread().getId(), null);

    }


}
