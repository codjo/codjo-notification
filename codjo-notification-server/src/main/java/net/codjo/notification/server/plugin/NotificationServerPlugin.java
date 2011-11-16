package net.codjo.notification.server.plugin;
import net.codjo.agent.AgentContainer;
import net.codjo.agent.ContainerConfiguration;
import net.codjo.agent.UserId;
import net.codjo.notification.common.message.NotificationRequest;
import net.codjo.notification.server.audit.NotificationStringifier;
import net.codjo.plugin.server.ServerPlugin;
import net.codjo.workflow.common.schedule.ScheduleLauncher;
import net.codjo.workflow.server.audit.AuditDao;
import net.codjo.workflow.server.plugin.WorkflowServerPlugin;
/**
 *
 */
public final class NotificationServerPlugin implements ServerPlugin {
    private NotificationServerOperations operations = new NotificationServerOperationsImpl();
    private AgentContainer container;
    private WorkflowServerPlugin workflowPlugin;


    public NotificationServerPlugin(WorkflowServerPlugin workflowPlugin) {
        this.workflowPlugin = workflowPlugin;

        new NotificationStringifier().install(workflowPlugin);
    }


    public void initContainer(ContainerConfiguration configuration) throws Exception {
    }


    public void start(AgentContainer agentContainer) throws Exception {
        AuditDao auditDao = workflowPlugin.getConfiguration().getAuditDao();
        agentContainer.acceptNewAgent("notify-job-agent", new NotificationJobAgent(auditDao)).start();
        container = agentContainer;
    }


    public void stop() throws Exception {
    }


    public NotificationServerOperations getOperations() {
        return operations;
    }


    public class NotificationServerOperationsImpl implements NotificationServerOperations {
        private static final String ERROR_MESSAGE
              = "Erreur technique bloquant l'envoie de la notification : ";


        public void sendNotification(UserId initiatorId, NotificationRequest notificationRequest)
              throws SendNotificationException {
            try {
                ScheduleLauncher scheduleLauncher = new ScheduleLauncher(initiatorId);
                scheduleLauncher.setExecuteType(ScheduleLauncher.ExecuteType.ASYNCHRONOUS);

                scheduleLauncher.executeWorkflow(container, notificationRequest.toRequest());
            }
            catch (Throwable e) {
                throw new SendNotificationException(ERROR_MESSAGE + e.getLocalizedMessage(), e);
            }
        }
    }
}
