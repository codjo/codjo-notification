package net.codjo.notification.server.plugin;
/**
 *
 */
public class SendNotificationException extends RuntimeException {

    public SendNotificationException() {
    }


    public SendNotificationException(String message) {
        super(message);
    }


    public SendNotificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
