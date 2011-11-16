package net.codjo.notification.server.plugin;
import net.codjo.agent.AclMessage;
import static net.codjo.agent.AclMessage.Performative.INFORM;
import static net.codjo.agent.AclMessage.Performative.REQUEST;
import net.codjo.agent.Agent;
import net.codjo.agent.Aid;
import net.codjo.agent.MessageTemplate;
import static net.codjo.agent.MessageTemplate.matchPerformative;
import net.codjo.agent.ServiceHelper;
import net.codjo.agent.ServiceMock;
import net.codjo.agent.UserId;
import net.codjo.agent.protocol.RequestProtocol;
import net.codjo.agent.test.AgentAssert;
import net.codjo.agent.test.AssertMatchExpression;
import net.codjo.agent.test.Story;
import static net.codjo.notification.common.message.NotificationRequest.NOTIFY_JOB_TYPE;
import net.codjo.security.common.api.UserData;
import net.codjo.security.common.api.UserDataMock;
import net.codjo.security.server.api.SecurityServiceHelper;
import net.codjo.security.server.api.SecurityServiceHelperMock;
import net.codjo.test.common.LogString;
import net.codjo.workflow.common.message.JobRequest;
import net.codjo.workflow.server.audit.AuditDaoMock;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import junit.framework.TestCase;
/**
 *
 */
public class NotificationJobAgentTest extends TestCase {
    private LogString log = new LogString();
    private Story story = new Story();
    private UserId userId = UserId.createId("smith", "secret");
    @SuppressWarnings({"StaticNonFinalField"})
    private static UserData userData = null;
    private AuditDaoMock auditDao = new AuditDaoMock(new LogString("auditDao", log));


    public void test_getOfflineNotification_firstConnection() throws Exception {
        userData = new UserDataMock(userId.getLogin(), new Date());

        story.installService(MySecurityService.class);

        story.record().startAgent("notifier-agent", createNotificationAgent());

        story.record().assertAgentWithService(new String[]{"notifier-agent"}, NOTIFY_JOB_TYPE);

        story.record().startTester("gui")
              .sendMessage(requestOfflineNotification())
              .then()
              .receiveMessage()
              .assertReceivedMessage(matchPerformative(INFORM))
              .assertReceivedMessage(contentListEquals(null));

        story.execute();
    }


    public void test_getOfflineNotification_oneRequest() throws Exception {
        userData = new UserDataMock(userId.getLogin(), new Date(10000), new Date(0));

        story.installService(MySecurityService.class);

        story.record().startAgent("notifier-agent", createNotificationAgent());

        story.record().assertAgentWithService(new String[]{"notifier-agent"}, NOTIFY_JOB_TYPE);

        story.record().startTester("gui")
              .sendMessage(requestOfflineNotification())
              .then()
              .receiveMessage()
              .assertReceivedMessage(matchPerformative(INFORM))
              .assertReceivedMessage(contentListEquals(new ArrayList<JobRequest>()));

        story.record().addAssert(AgentAssert.log(log, "auditDao.findRequest(notify, "
                                                      + "Thu Jan 01 01:00:00 CET 1970, "
                                                      + "Thu Jan 01 01:00:10 CET 1970)"));

        story.execute();
    }


    public void test_getOfflineNotification_oneWeekOnly() throws Exception {
        userData = new UserDataMock(userId.getLogin(),
                                    toDate("2007-05-01"),
                                    toDate("2007-01-01"));

        story.installService(MySecurityService.class);

        story.record().startAgent("notifier-agent", createNotificationAgent());

        story.record().assertAgentWithService(new String[]{"notifier-agent"}, NOTIFY_JOB_TYPE);

        story.record().startTester("gui")
              .sendMessage(requestOfflineNotification())
              .then()
              .receiveMessage()
              .assertReceivedMessage(matchPerformative(INFORM))
              .assertReceivedMessage(contentListEquals(new ArrayList<JobRequest>()));

        story.record().addAssert(AgentAssert.log(log, "auditDao.findRequest(notify, "
                                                      + "Tue Apr 24 00:00:00 CEST 2007, "
                                                      + "2007-05-01)"));

        story.execute();
    }


    private java.sql.Date toDate(String date) {
        return java.sql.Date.valueOf(date);
    }


    @Override
    protected void setUp() throws Exception {
        story.doSetUp();
    }


    @Override
    protected void tearDown() throws Exception {
        story.doTearDown();
    }


    private NotificationJobAgent createNotificationAgent() {
        return new NotificationJobAgent(auditDao);
    }


    private AclMessage requestOfflineNotification() {
        AclMessage request = new AclMessage(REQUEST);
        request.setContent("getOfflineNotification");
        request.addReceiver(new Aid("notifier-agent"));
        request.setProtocol(RequestProtocol.REQUEST);
        request.encodeUserId(userId);
        return request;
    }


    private MessageTemplate contentListEquals(List<JobRequest> expected) {
        return MessageTemplate.matchWith(new AssertMatchExpression("requestList", expected) {
            @Override
            protected Object extractActual(AclMessage message) {
                return message.getContentObject();
            }
        });
    }


    public static class MySecurityService extends ServiceMock {

        @Override
        public String getName() {
            return SecurityServiceHelper.NAME;
        }


        @Override
        public ServiceHelper getServiceHelper(Agent agent) {
            return new SecurityServiceHelperMock().mockGetUserData(userData);
        }
    }
}
