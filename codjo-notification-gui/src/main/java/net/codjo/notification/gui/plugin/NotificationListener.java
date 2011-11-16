package net.codjo.notification.gui.plugin;
import net.codjo.notification.common.message.NotificationRequest;
/**
 *
 */
interface NotificationListener {
    public void receive(NotificationRequest notification);
}
