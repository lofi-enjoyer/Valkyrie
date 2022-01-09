package me.aurgiyalgo.nublada.log;

import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class NubladaLogHandler extends Handler {

    public static Logger initLogs() {
        Logger logger;
        LogManager.getLogManager().reset();
        logger = Logger.getLogger("Nublada");
        logger.addHandler(new NubladaLogHandler());
        return logger;
    }

    @Override
    public void publish(LogRecord record) {
        String sb = "[" + record.getLevel().getName() + "] ("
                + record.getSourceClassName() +
                "#" +
                record.getSourceMethodName() +
                ") "
                + record.getMessage();
        System.out.println(sb);
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }

}
