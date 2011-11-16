package net.codjo.notification.gui.plugin;
import net.codjo.agent.AclMessage;
import net.codjo.agent.MessageTemplate;
import net.codjo.agent.UserId;
import net.codjo.agent.protocol.RequestProtocol;
import net.codjo.agent.test.AgentAssert;
import net.codjo.agent.test.AssertMatchExpression;
import net.codjo.agent.test.Story;
import net.codjo.notification.common.message.NotificationRequest;
import static net.codjo.notification.common.message.NotificationRequest.NOTIFY_JOB_TYPE;
import net.codjo.notification.gui.api.NotificationHandler;
import net.codjo.test.common.LogString;
import net.codjo.workflow.common.message.JobRequest;
import java.io.Serializable;
import java.util.ArrayList;
import junit.framework.TestCase;
/**
 *
 */
public class NotificationGuiAgentTest extends TestCase {
    private Story story = new Story();
    private UserId userId = UserId.createId("jason", "toison");
    private LogString log = new LogString();
    private NotificationManager notificationManager = new NotificationManager();
    private NotificationGuiAgent notificationGuiAgent = new NotificationGuiAgent(userId, notificationManager);


    @Override
    protected void setUp() throws Exception {
        story.doSetUp();
        notificationManager.setDefaultNotificationHandler(new NotificationHandler() {
            public void handle(NotificationRequest notification) {
                log.call("handle", notification.getNotificationId());
            }
        });
    }


    @Override
    protected void tearDown() throws Exception {
        story.doTearDown();
    }


    public void test_firstLogin() throws Exception {

        story.record().startTester("server-agent")
              .regsiterToDF(NOTIFY_JOB_TYPE)
              .then()
              .receiveMessage()
              .assertReceivedMessage(MessageTemplate.matchPerformative(AclMessage.Performative.REQUEST))
              .assertReceivedMessage(MessageTemplate.matchProtocol(RequestProtocol.REQUEST))
              .assertReceivedMessage(MessageTemplate.matchContent("getOfflineNotification"))
              .assertReceivedMessage(MessageTemplate.matchWith(new AssertMatchExpression("user-id", userId) {
                  @Override
                  protected Object extractActual(AclMessage message) {
                      return message.decodeUserId();
                  }
              }))
              .replyWithContent(AclMessage.Performative.INFORM, null);

        story.record().assertAgentWithService(new String[]{"server-agent"}, NOTIFY_JOB_TYPE);

        story.record().startAgent("me", notificationGuiAgent);

        story.record().addAssert(AgentAssert.log(log, "handle(displayWelcomeMessage)"));

        story.execute();
    }


    public void test_receiveOfflineNotification() throws Exception {
        story.record().startTester("server-agent")
              .regsiterToDF(NOTIFY_JOB_TYPE)
              .then()
              .receiveMessage()
              .replyWithContent(AclMessage.Performative.INFORM, toList("my-notification"));

        story.record().assertAgentWithService(new String[]{"server-agent"}, NOTIFY_JOB_TYPE);

        story.record().startAgent("me", notificationGuiAgent);

        story.record().addAssert(AgentAssert.log(log, "handle(my-notification)"));

        story.execute();
    }


    private Serializable toList(String taskId) {
        //noinspection CollectionDeclaredAsConcreteClass
        ArrayList<JobRequest> list = new ArrayList<JobRequest>();
        list.add(new NotificationRequest(taskId).toRequest());
        return list;
    }
}
