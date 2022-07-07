package game.engine;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Consumer;

public final class Logger {
    private Logger(){}

    private static Consumer<String> errorHandler = System.err::println;
    private static Consumer<String> warningHandler = System.out::println;
    private static Consumer<String> messageHandler = System.out::println;

    public static void error(Exception error) {
        error(error.getMessage());
    }
    public static void error(String error){
        Logger.errorHandler.accept(formatError(error));
    }

    public static void warn(String warning){
        Logger.warningHandler.accept(formatWarning(warning));
    }

    public static void log(String message){
        Logger.messageHandler.accept(formatMessage(message));
    }

    private static String formatMessage(String message){
        return LocalTime.now() + ":" + message;
    }
    private static String formatError(String message){
        return LocalTime.now() + ":ERROR:\"" + message.toUpperCase();
    }

    private static String formatWarning(String message) {
        return LocalTime.now() + ":Warning:\"" + message;
    }

    public static void setErrorHandler(Consumer<String> handler){
        Logger.errorHandler = handler;
    }

    public static void setWarningHandler(Consumer<String> handler){
        Logger.warningHandler = handler;
    }

    public static void setMessageHandler(Consumer<String> handler){
        Logger.messageHandler = handler;
    }
}
