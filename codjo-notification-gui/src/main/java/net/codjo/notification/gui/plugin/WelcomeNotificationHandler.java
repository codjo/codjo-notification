package net.codjo.notification.gui.plugin;
import net.codjo.notification.common.message.NotificationRequest;
import net.codjo.notification.gui.api.AbstractNotificationHandler;
/**
 * Tache par défaut affichant un message de bienvenue !
 */
class WelcomeNotificationHandler extends AbstractNotificationHandler {
    private static final String WELCOME_TEXT = "<html><body>"
                                               + "<center><h1>Bienvenue !</h1></center>"
                                               + "<center><h3>Information</h3></center>"
                                               + "Regardez dans la table des logs !"
                                               + "</body></html>";


    WelcomeNotificationHandler(NotificationGuiOperations notificationGuiOperations) {
        super(notificationGuiOperations);
    }


    public void handle(NotificationRequest notification) {
        displayNotification("Bienvenue !", WELCOME_TEXT);
    }
}
