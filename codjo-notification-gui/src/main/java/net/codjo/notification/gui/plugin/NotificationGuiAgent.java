package net.codjo.notification.gui.plugin;
import net.codjo.agent.AclMessage;
import static net.codjo.agent.AclMessage.Performative.REQUEST;
import net.codjo.agent.Agent;
import net.codjo.agent.Aid;
import net.codjo.agent.DFService;
import net.codjo.agent.UserId;
import net.codjo.agent.protocol.InitiatorHandler;
import net.codjo.agent.protocol.RequestInitiator;
import net.codjo.agent.protocol.RequestProtocol;
import net.codjo.notification.common.message.NotificationRequest;
import static net.codjo.notification.common.message.NotificationRequest.NOTIFY_JOB_TYPE;
import static net.codjo.notification.gui.plugin.NotificationGuiPlugin.WELCOME_MESSAGE_NOTIFICATION;
import net.codjo.workflow.common.message.JobRequest;
import java.util.List;
import org.apache.log4j.Logger;
/**
 *
 */
public class NotificationGuiAgent extends Agent implements InitiatorHandler {
    private static final Logger LOG = Logger.getLogger(NotificationGuiAgent.class);
    private final NotificationManager notificationManager;
    private final UserId userId;


    public NotificationGuiAgent(UserId userId, NotificationManager notificationManager) {
        this.userId = userId;
        this.notificationManager = notificationManager;
    }


    @Override
    protected void setup() {
        Aid aid = findNotifyAgent();
        if (aid == null) {
            die();
            return;
        }
        AclMessage request = new AclMessage(REQUEST);
        request.addReceiver(aid);
        request.encodeUserId(userId);
        request.setProtocol(RequestProtocol.REQUEST);
        request.setContent("getOfflineNotification");

        addBehaviour(new RequestInitiator(this, this, request));
        LOG.info("Demande des notifications offlines au serveur...");
    }


    @Override
    protected void tearDown() {
    }


    private Aid findNotifyAgent() {
        try {
            DFService.AgentDescription[] descriptions = DFService.searchForService(this, NOTIFY_JOB_TYPE);

            if (descriptions.length == 0) {
                LOG.warn("Impossible de trouver l'agent gérant les notifications (absent de la plateforme)");
                return null;
            }
            return descriptions[0].getAID();
        }
        catch (DFService.DFServiceException e) {
            LOG.warn("Impossible de trouver l'agent gérant les notifications", e);
            return null;
        }
    }


    public void handleAgree(AclMessage agree) {
    }


    public void handleRefuse(AclMessage refuse) {
        closeWithError("handleRefuse");
    }


    public void handleInform(AclMessage inform) {
        //noinspection unchecked
        List<JobRequest> result = (List<JobRequest>)inform.getContentObject();
        if (result == null) {
            notificationManager.receive(new NotificationRequest(WELCOME_MESSAGE_NOTIFICATION));
        }
        else {
            for (JobRequest notification : result) {
                notificationManager.receive(new NotificationRequest(notification));
            }
        }
        die();
    }


    public void handleFailure(AclMessage failure) {
        closeWithError("handleFailure");
    }


    public void handleOutOfSequence(AclMessage outOfSequenceMessage) {
        closeWithError("handleOutOfSequence");
    }


    public void handleNotUnderstood(AclMessage notUnderstoodMessage) {
        closeWithError("handleNotUnderstood");
    }


    private void closeWithError(String step) {
        die();
        String errorMessage = "Recuperation des notifications offline en erreur (" + step + ")";
        LOG.warn(errorMessage);
        NotificationRequest request = new NotificationRequest("failure");
        request.getArguments().put("message", errorMessage);
        notificationManager.receive(request);
    }
}
