/**
 *
 */
package com.lafaspot.pop.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;

import com.lafaspot.logfast.logging.Logger;
import com.lafaspot.pop.command.PopResponse;
import com.lafaspot.pop.exception.PopException;
import com.lafaspot.pop.session.PopSession;

/**
 * Class to decode messages from POP server.
 *
 * @author kraman
 *
 */
public class PopMessageDecoder extends MessageToMessageDecoder<String> {

    private final PopSession session;

    public PopMessageDecoder(@Nonnull PopSession session, @Nonnull Logger logger) {
        this.session = session;
    }

    @Override
    protected void decode(final ChannelHandlerContext ctx, final String msg, final List<Object> out) throws IOException {
        try {
            final PopResponse oldResp = session.getCurrentResponse();
            if (null != oldResp) {
                if (oldResp.parseComplete()) {
                    final PopResponse newResp = new PopResponse(msg);
                    session.setCurrentResponse(newResp);
                    out.add(newResp);
                } else {
                    oldResp.parse(msg);
                }
            }
        } catch (PopException e) {
            // move on
        }
    }
}
