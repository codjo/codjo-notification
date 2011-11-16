package net.codjo.notification.server.plugin;
import net.codjo.agent.UserId;
import net.codjo.notification.common.message.NotificationRequest;
/**
 *
 */
public interface NotificationServerOperations {
    public void sendNotification(UserId initiatorId, NotificationRequest notificationRequest)
          throws SendNotificationException;
}
