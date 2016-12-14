/**
 *
 */
package com.lafaspot.pop.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import com.lafaspot.logfast.logging.LogContext;
import com.lafaspot.logfast.logging.LogManager;
import com.lafaspot.logfast.logging.Logger;
import com.lafaspot.pop.netty.PopClientInitializer;
import com.lafaspot.pop.session.PopSession;
import com.lafaspot.pop.session.SessionLogContext;

/**
 * POP client that supports secure connection and POP3 protocol.
 *
 * @author kraman
 *
 */
public class PopClient {

    /** instance id used for debug. */
    private final String instanceId = Integer.toString(new Random(System.nanoTime()).nextInt());

    /** counter for sessions. */
    private AtomicInteger sessionCounter = new AtomicInteger(1);

    /** The netty bootstrap. */
    private final Bootstrap bootstrap;

    /** Event loop group that will serve all channels for IMAP client. */
    private final EventLoopGroup group;

    /** The log manger. */
    private final LogManager logManager;

    /** The logger. */
    private Logger logger;

    /**
     * Constructor to create a new POP client.
     *
     * @param threads number of threads to use
     * @param logManager the log manager
     */
    public PopClient(final int threads, @Nonnull final LogManager logManager) {

        try {
            this.bootstrap = new Bootstrap();
            this.group = new NioEventLoopGroup(threads);
            bootstrap.group(group).channel(NioSocketChannel.class).handler(new PopClientInitializer());

            this.logManager = logManager;
            LogContext context = new SessionLogContext("PopClient");
            this.logger = logManager.getLogger(context);
        } finally {
            this.group.shutdownGracefully();
        }
    }

    public PopSession createSession(@Nonnull final String server, final int port) {
        return new PopSession(bootstrap, server, port, logger);
    }

}
