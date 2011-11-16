package net.codjo.notification.common.message;
import net.codjo.workflow.common.message.JobRequest;
import net.codjo.workflow.common.message.JobRequestWrapper;
import net.codjo.workflow.common.message.JobRequestWrapperTestCase;
/**
 *
 */
public class NotificationRequestTest extends JobRequestWrapperTestCase {

    public void test_getTaskId() throws Exception {
        NotificationRequest first = new NotificationRequest("welcome-new-user");
        assertEquals("welcome-new-user", first.getNotificationId());

        NotificationRequest second = new NotificationRequest(first.toRequest());
        assertEquals("welcome-new-user", second.getNotificationId());
    }


    public void test_toString() throws Exception {
        assertNotNull(new NotificationRequest("welcome-new-user").toString());
        assertNotNull(new NotificationRequest(new JobRequest().toString()));
    }


    protected String getJobRequestType() {
        return NotificationRequest.NOTIFY_JOB_TYPE;
    }


    protected JobRequestWrapper createWrapper(JobRequest jobRequest) {
        return new NotificationRequest(jobRequest);
    }
}
