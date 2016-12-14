/**
 *
 */
package com.lafaspot.pop.client;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.lafaspot.logfast.logging.LogContext;
import com.lafaspot.logfast.logging.LogManager;
import com.lafaspot.logfast.logging.Logger;
import com.lafaspot.logfast.logging.Logger.Level;
import com.lafaspot.pop.exception.PopException;
import com.lafaspot.pop.session.PopSession;

/**
 * @author kraman
 *
 */
public class PopClientIT {

    private PopClient client;
    private LogManager logManager;
    private Logger logger;

    @BeforeClass
    public void beforeClass() {

        logManager = new LogManager(Level.DEBUG, 5);
        logManager.setLegacy(true);
        logger = logManager.getLogger(new LogContext(PopClientIT.class.getName()) {
        });
        client = new PopClient(10, logManager);
    }

    @Test
    public void testConnect() throws PopException, InterruptedException, ExecutionException {
        PopSession session = client.createSession("jpop.pop.mail.yahoo.com", 995);
        Future<Boolean> f = session.connect(30000, 60000);
        Assert.assertTrue(f.get());
    }

}
