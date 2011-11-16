package net.codjo.notification.common.message;
import net.codjo.workflow.common.message.JobRequest;
import net.codjo.workflow.common.message.JobRequestWrapper;
/**
 * Encapsule un JobRequest de notification.
 */
public class NotificationRequest extends JobRequestWrapper {
    public static final String NOTIFY_JOB_TYPE = "notify";
    private static final String NOTIFICATION_ID = "notification-id";


    public NotificationRequest(String taskId) {
        this(new JobRequest());
        setNotificationId(taskId);
    }


    public NotificationRequest(JobRequest jobRequest) {
        super(NOTIFY_JOB_TYPE, jobRequest);
    }


    public void setNotificationId(String taskId) {
        setArgument(NOTIFICATION_ID, taskId);
    }


    public String getNotificationId() {
        return getArgument(NOTIFICATION_ID);
    }


    @Override
    public String toString() {
        return "Tâche : " + getNotificationId() + " (" + getArguments().encode() + ")";
    }
}
