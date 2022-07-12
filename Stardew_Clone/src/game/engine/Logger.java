package game.engine;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class Logger {

    private Logger() {}
    private static Logger instance = null;
    public static Logger getInstance(){
        if(instance == null){
            instance = new Logger();
        }
        return instance;
    }
    public void error(Exception error) {
        error(error.getMessage());
    }
    public void error(String error){
        this.handleError(formatError(error));
    }

    public void warn(String warning){
        this.handleWarning(formatWarning(warning));
    }

    public void log(String message){
        this.handleMessage(formatMessage(message));
    }

    private void handleMessage(String message){
        System.out.println(message);
    }

    private void handleWarning(String warning){
        System.out.println(warning);
    }

    private void handleError(String error){
        System.err.println(error);
    }

    private String formatMessage(String message){
        return LocalTime.now().truncatedTo(ChronoUnit.MILLIS) + ":" + message;
    }
    private String formatError(String message){
        return LocalTime.now().truncatedTo(ChronoUnit.MILLIS) + ":ERROR:\"" + message.toUpperCase();
    }

    private String formatWarning(String message) {
        return LocalTime.now().truncatedTo(ChronoUnit.MILLIS) + ":Warning:\"" + message;
    }
}
