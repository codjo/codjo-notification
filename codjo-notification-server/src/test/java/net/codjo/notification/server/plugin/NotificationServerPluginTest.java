package net.codjo.notification.server.plugin;
import net.codjo.agent.UserId;
import net.codjo.agent.test.AgentContainerFixture;
import net.codjo.agent.test.Story;
import net.codjo.notification.common.message.NotificationRequest;
import static net.codjo.workflow.common.util.WorkflowSystem.workFlowSystem;
import net.codjo.workflow.server.plugin.WorkflowServerPlugin;
import junit.framework.TestCase;

public class NotificationServerPluginTest extends TestCase {
    private NotificationServerPlugin plugin;
    private Story story = new Story();
    private static final UserId USER_ID = UserId.createId("james", "secret");


    @Override
    protected void setUp() throws Exception {
        story.doSetUp();
        plugin = new NotificationServerPlugin(new WorkflowServerPlugin());
    }


    @Override
    protected void tearDown() throws Exception {
        story.doTearDown();
    }


    public void test_getOperations() throws Exception {
        assertNotNull(plugin.getOperations());

        story.record().mock(workFlowSystem())
              .simulateJob("job<notify>(notification-id=do-stuff)")
              .forUser(USER_ID);

        story.record().addAction(new AgentContainerFixture.Runnable() {
            public void run() throws Exception {
                plugin.start(story.getContainer());
                plugin.getOperations().sendNotification(USER_ID, new NotificationRequest("do-stuff"));
            }
        });

        story.execute();
    }
}
