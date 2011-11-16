package net.codjo.notification.gui.plugin;
import net.codjo.notification.common.message.NotificationRequest;
import net.codjo.notification.gui.api.AbstractNotificationHandler;
/**
 * Tache par défaut affichant les taches non définit.
 */
final class DefaultNotificationHandler extends AbstractNotificationHandler {

    DefaultNotificationHandler(NotificationGuiOperations notificationGuiOperations) {
        super(notificationGuiOperations);
    }


    public void handle(NotificationRequest notification) {
        displayNotification("Message du serveur inconnu", notification.toString());
    }
}
