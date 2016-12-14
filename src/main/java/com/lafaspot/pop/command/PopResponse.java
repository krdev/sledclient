/**
 *
 */
package com.lafaspot.pop.command;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.lafaspot.pop.exception.PopException;

/**
 * Object representing response from server.
 *
 * @author kraman
 *
 */
public class PopResponse {


    private static final byte PLUS_BYTE = '+';
    private static final byte MINUS_BYTE = '-';
    private static final String DOT_STRING = ".";

    /** Is the response a success or failure. */
    private ResponseType type;

    /** Response parsed into individual lines. */
    private final List<String> lines;

    /** Is parsing complete. */
    private boolean parseComplete;

    private final ByteBuffer buffer;

    /** max buffer size 64 K */
    private final int MAX_BUFFER_SIZE = 64 * 1024;

    /** read index marker. */
    private int readIndex;

    /** write index marker. */
    private int writeIndex;

    /**
     * Constructor to create PopResponse.
     *
     * @param response the response string
     * @throws PopException
     */
    public PopResponse(@Nonnull final String response) throws PopException {
        this.buffer = ByteBuffer.allocate(MAX_BUFFER_SIZE);
        this.buffer.put(response.getBytes(StandardCharsets.UTF_8));
        readIndex = 0;
        writeIndex = buffer.position();
        this.lines = new ArrayList<String>();
        this.parseComplete = false;
        firstParse();
    }

    /**
     * Parses the status line.
     *
     * @throws PopException
     */
    void firstParse() throws PopException {
        int idx = readIndex;
        if (idx < writeIndex) {
            byte b1 = buffer.get(idx);
            if (b1 == PLUS_BYTE) {
                type = ResponseType.OK;
            } else if (b1 == MINUS_BYTE) {
                type = ResponseType.ERR;
            } else {
                throw new PopException(PopException.Type.PARSE_FAILURE);
            }
            parse();
        } else {
            throw new PopException(PopException.Type.PARSE_FAILURE);
        }
    }

    /**
     * Incremental parsing, looking for newline delimiters and end of stream char DOT ('.').
     */
    void parse() {
        int idx = readIndex;
        final StringBuffer line = new StringBuffer();
        while (idx < writeIndex) {
            byte b1 = buffer.get(idx);
            if (idx + 1 >= writeIndex) {
                readIndex = idx;
                break;
            }
            byte b2 = buffer.get(idx+1);
            if (b1 == '\r' && b2 == '\n') {
                lines.add(line.toString());
                readIndex = idx + 2;
            } else {
                line.append(b1);
                readIndex = ++idx;
            }
        }

        if (readIndex == writeIndex) {
            if (line.toString().equals(DOT_STRING)) {
                parseComplete = true;
            }
        } else {
            // reset
            byte remaining[] = new byte[writeIndex - readIndex];
            buffer.position(readIndex);
            buffer.get(remaining);
            buffer.position(0);
            buffer.put(remaining);
            readIndex = 0;
            writeIndex = buffer.position();
        }
    }

    /**
     * Parse response from the channel.
     *
     * @param res incoming response
     * @throws PopException when response too large to parse
     */
    public void parse(@Nonnull final String res) throws PopException {
        byte bytes[] = res.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > (buffer.capacity() - buffer.position())) {
            throw new PopException(PopException.Type.PARSE_FAILURE);
        }
        buffer.put(bytes);
        writeIndex = buffer.position();
        parse();
    }

    public boolean parseComplete() {
        return parseComplete;
    }

    public ResponseType getResponseType() {
        return type;
    }

    /**
     * @return the command
     */
    public List<String> getResponse() {
        return lines;
    }

    /**
     * Enum representing the response type, success or failure.
     *
     * @author kraman
     *
     */
    public enum ResponseType {
        /** success. */
        OK,
        /** failure. */
        ERR
    }

}
