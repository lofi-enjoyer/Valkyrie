package me.lofienjoyer.nublada.engine.log;

import java.util.logging.*;

public class NubladaLogHandler extends Handler {

    // FIXME: 09/01/2022 make this useful

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
                + parseClass(record) +
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

    private String parseClass(LogRecord record) {
        String[] parts = record.getSourceClassName().split("\\.");
        String parsedClass = "";
        for (int i = 0; i < parts.length - 1; i++) {
            parsedClass += parts[i].charAt(0) + ".";
        }
        parsedClass += parts[parts.length - 1];
        return parsedClass;
    }

}
