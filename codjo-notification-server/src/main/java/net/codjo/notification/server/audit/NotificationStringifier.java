package net.codjo.notification.server.audit;
import net.codjo.notification.common.message.NotificationRequest;
import net.codjo.workflow.common.message.JobRequest;
import net.codjo.workflow.server.plugin.StringifierImpl;
/**
 *
 */
public class NotificationStringifier extends StringifierImpl {

    public NotificationStringifier() {
        super(NotificationRequest.NOTIFY_JOB_TYPE);
    }


    public String toString(JobRequest jobRequest) {
        return new NotificationRequest(jobRequest).getNotificationId();
    }
}