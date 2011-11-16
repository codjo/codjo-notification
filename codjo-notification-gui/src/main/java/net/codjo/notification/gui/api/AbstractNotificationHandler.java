package net.codjo.notification.gui.api;
import net.codjo.notification.gui.plugin.NotificationGuiOperations;
/**
 *
 */
public abstract class AbstractNotificationHandler implements NotificationHandler {
    protected final NotificationGuiOperations notificationGuiOperations;


    protected AbstractNotificationHandler(NotificationGuiOperations notificationGuiOperations) {
        this.notificationGuiOperations = notificationGuiOperations;
    }


    public NotificationGuiOperations getNotificationGuiOperations() {
        return notificationGuiOperations;
    }


    protected void displayNotification(String title, String message) {
        notificationGuiOperations.displayNotification(title, message);
    }
}
