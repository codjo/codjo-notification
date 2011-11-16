package net.codjo.notification.server.plugin;
import net.codjo.agent.AclMessage;
import static net.codjo.agent.AclMessage.Performative.REQUEST;
import net.codjo.agent.DFService;
import net.codjo.agent.MessageTemplate;
import static net.codjo.agent.MessageTemplate.and;
import static net.codjo.agent.MessageTemplate.matchPerformative;
import static net.codjo.agent.MessageTemplate.matchProtocol;
import net.codjo.agent.ServiceException;
import net.codjo.agent.UserId;
import net.codjo.agent.behaviour.CyclicBehaviour;
import net.codjo.agent.protocol.RequestProtocol;
import net.codjo.notification.common.message.NotificationRequest;
import static net.codjo.notification.common.message.NotificationRequest.NOTIFY_JOB_TYPE;
import net.codjo.security.common.api.UserData;
import net.codjo.security.server.api.SecurityServiceHelper;
import net.codjo.workflow.common.message.JobException;
import net.codjo.workflow.common.message.JobRequest;
import net.codjo.workflow.common.protocol.JobProtocolParticipant;
import net.codjo.workflow.server.api.JobAgent;
import net.codjo.workflow.server.audit.AuditDao;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
/**
 *
 */
class NotificationJobAgent extends JobAgent {
    private static final Logger LOG = Logger.getLogger(NotificationJobAgent.class);
    static final String NOTIFY_SERVICE = "notify-service";
    private final AuditDao auditDao;


    NotificationJobAgent(AuditDao auditDao) {
        this.auditDao = auditDao;
        setAgentDescription(getNotificationDescription());
        setJobProtocolParticipant(new NotifyParticipant());
    }


    @Override
    protected void setup() {
        super.setup();
        addBehaviour(new GetOfflineNotificationBehaviour());
    }


    private DFService.AgentDescription getNotificationDescription() {
        return new DFService.AgentDescription(
              new DFService.ServiceDescription(NOTIFY_JOB_TYPE, NOTIFY_SERVICE));
    }


    private static class NotifyParticipant extends JobProtocolParticipant {
        @Override
        protected void executeJob(JobRequest request) throws JobException {
            LOG.info("Notification '" + new NotificationRequest(request) + "'");
        }
    }
    private class GetOfflineNotificationBehaviour extends CyclicBehaviour {
        private MessageTemplate template;


        private GetOfflineNotificationBehaviour() {
            template = and(matchPerformative(REQUEST), matchProtocol(RequestProtocol.REQUEST));
        }


        @Override
        protected void action() {
            AclMessage request = getAgent().receive(template);
            if (request == null) {
                block();
                return;
            }

            UserId userId = request.decodeUserId();

            try {
                AclMessage reply = request.createReply(AclMessage.Performative.INFORM);

                UserData user = getSecurityService().getUserData(userId, userId.getLogin());

                if (user.getLastLogout() != null) {

                    Date start = computeBeginDate(user);
                    Date to = user.getLastLogin();
                    List<JobRequest> requests =
                          auditDao.findRequest(getAgent(), request, NOTIFY_JOB_TYPE, start, to);

                    LOG.info("Recuperation des " + requests.size()
                             + " notifications offline pour '" + userId.getLogin() + "'"
                             + " du " + start + " au " + to);

                    reply.setContentObject(new ArrayList<JobRequest>(requests));
                }
                else {
                    LOG.info("Recuperation des notifications offline pour '" + userId.getLogin() + "'"
                             + " (annule car premiere connexion)");
                }

                getAgent().send(reply);
            }
            catch (Throwable e) {
                LOG.warn("Impossible de determiner les notifications offline pour "
                         + userId.getLogin(),
                         e);
                getAgent().send(request.createReply(AclMessage.Performative.FAILURE));
            }
        }


        private Date computeBeginDate(UserData user) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(user.getLastLogin());
            calendar.add(Calendar.DAY_OF_MONTH, -7);

            Date sevenDaysBeforeDate = calendar.getTime();

            if (sevenDaysBeforeDate.after(user.getLastLogout())) {
                return sevenDaysBeforeDate;
            }
            return user.getLastLogout();
        }


        private SecurityServiceHelper getSecurityService() throws ServiceException {
            return ((SecurityServiceHelper)getAgent().getHelper(SecurityServiceHelper.NAME));
        }
    }
}
