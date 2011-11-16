package net.codjo.notification.server.audit;
import net.codjo.workflow.common.message.Arguments;
import net.codjo.workflow.common.message.JobRequest;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
/**
 *
 */
public class NotificationStringifierTest {
    private NotificationStringifier stringifier = new NotificationStringifier();


    @Test
    public void test_toString() throws Exception {
        Arguments arguments = new Arguments("notification-id", "some task");

        assertEquals("some task", stringifier.toString(new JobRequest("notify", arguments)));
    }
}
