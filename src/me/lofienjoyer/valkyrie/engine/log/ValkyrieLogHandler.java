package me.lofienjoyer.valkyrie.engine.log;

import java.util.logging.*;

public class ValkyrieLogHandler extends Handler {

    public static Logger initLogs() {
        Logger logger;
        LogManager.getLogManager().reset();
        logger = Logger.getLogger("Valkyrie");
        logger.addHandler(new ValkyrieLogHandler());
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
