/**
 *
 */
package com.lafaspot.pop.session;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.util.concurrent.Future;

import javax.annotation.Nonnull;
import javax.net.ssl.SSLException;

import com.lafaspot.logfast.logging.Logger;
import com.lafaspot.pop.command.PopCommand;
import com.lafaspot.pop.command.PopResponse;
import com.lafaspot.pop.exception.PopException;
import com.lafaspot.pop.netty.PopMessageDecoder;

/**
 * @author kraman
 *
 */
public class PopSession {

    private State state;

    private final Bootstrap bootstrap;

    @Nonnull
    private final String server;

    private final int port;

    private final Logger logger;

    private Channel sessionChannel;

    private PopResponse currentResponse;

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
        } else if (!future.isDone()) { // future.isSuccess()) {
            throw new PopException(PopException.Type.CONNECT_FAILURE);
        } else {

            this.sessionChannel = future.channel();

            SslContext sslCtx;
            try {
                sslCtx = SslContextBuilder.forClient().build();
            } catch (SSLException e) {
                throw new PopException(PopException.Type.CONNECT_FAILURE, e);
            }
            this.sessionChannel.pipeline().addLast("ssl", sslCtx.newHandler(this.sessionChannel.alloc(), server, port));
            this.sessionChannel.pipeline().addLast("inactivityHandler", new PopInactivityHandler(this, inactivityTimeout, logger));
            this.sessionChannel.pipeline().addLast(new PopMessageDecoder(this, logger));
            // this.sessionChannel.pipeline().addLast(new IcapChannelHandler(this));
        }

        state = State.CONNECTED;

        PopFuture<Boolean> f = new PopFuture<Boolean>(future);
        f.done(Boolean.TRUE);
        return f;
    }


    public PopFuture<PopResponse> execute(@Nonnull final PopCommand command) throws PopException {

        if (state != State.CONNECTED) {
            throw new PopException(PopException.Type.INVALID_STATE);
        }

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

    public void setState(final State state) {
        this.state = state;
    }



    /**
     * @return the currentResponse
     */
    public PopResponse getCurrentResponse() {
        return currentResponse;
    }

    /**
     * @param currentResponse the currentResponse to set
     */
    public void setCurrentResponse(PopResponse currentResponse) {
        this.currentResponse = currentResponse;
    }



    public enum State {
        BULL, CONNECT_SENT, CONNECTED, COMMAND_SENT
    }


}
