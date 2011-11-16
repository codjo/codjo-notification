package net.codjo.notification.gui.plugin;
import net.codjo.notification.common.message.NotificationRequest;
import net.codjo.notification.gui.api.NotificationHandler;
import java.util.HashMap;
import java.util.Map;
/**
 * Manager des taches clientes.
 *
 * <p> NB : ne pas oublier de fermer le manager après utilisation. </p>
 */
class NotificationManager {
    private Map<String, NotificationHandler> clientTask = new HashMap<String, NotificationHandler>();
    private TaskMessageListener listener = new TaskMessageListener();
    private NotificationHandler defaultTask;


    NotificationListener getListener() {
        return listener;
    }


    public void addNotificationHandler(String taskId, NotificationHandler task) {
        clientTask.put(taskId, task);
    }


    void setDefaultNotificationHandler(NotificationHandler defaultNotificationHandler) {
        defaultTask = defaultNotificationHandler;
    }


    public void receive(NotificationRequest notificationRequest) {
        getListener().receive(notificationRequest);
    }


    /**
     * Ecoute les taches envoyées par le serveur.
     */
    private class TaskMessageListener implements NotificationListener {
        public void receive(NotificationRequest message) {
            NotificationHandler task = clientTask.get(message.getNotificationId());
            if (task != null) {
                task.handle(message);
            }
            else {
                defaultTask.handle(message);
            }
        }
    }
}
