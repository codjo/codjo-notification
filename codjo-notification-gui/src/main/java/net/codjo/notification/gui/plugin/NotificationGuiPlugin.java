package net.codjo.notification.gui.plugin;
import net.codjo.agent.AgentContainer;
import net.codjo.agent.AgentController;
import net.codjo.agent.UserId;
import net.codjo.mad.gui.base.AbstractGuiPlugin;
import net.codjo.mad.gui.base.GuiConfiguration;
import net.codjo.notification.common.message.NotificationRequest;
import net.codjo.notification.gui.api.NotificationHandler;
import net.codjo.plugin.common.ApplicationCore;
import net.codjo.workflow.common.message.JobRequest;
import net.codjo.workflow.common.message.JobRequestTemplate;
import net.codjo.workflow.common.subscribe.JobEventHandler;
import net.codjo.workflow.gui.util.JobListenerAgentUtil;
/**
 *
 */
public final class NotificationGuiPlugin extends AbstractGuiPlugin {
    static final String WELCOME_MESSAGE_NOTIFICATION = "displayWelcomeMessage";
    private NotificationManager notificationManager = new NotificationManager();
    private final NotificationGuiPluginConfiguration configuration = new NotificationGuiPluginConfiguration();
    private final NotificationGuiOperationsImpl operations = new NotificationGuiOperationsImpl();
    private AgentController listenerController;
    private final ApplicationCore applicationCore;
    private AgentContainer agentContainer;


    public NotificationGuiPlugin(ApplicationCore core) {
        this.applicationCore = core;
    }


    public void initGui(GuiConfiguration guiConfiguration) throws Exception {
        operations.setGuiContext(guiConfiguration.getGuiContext());

        notificationManager.setDefaultNotificationHandler(new DefaultNotificationHandler(operations));
        notificationManager.addNotificationHandler(WELCOME_MESSAGE_NOTIFICATION,
                                                   new WelcomeNotificationHandler(operations));

        listenerController = JobListenerAgentUtil.startListenerAgent(agentContainer, new NotifyJobListener());

        UserId userId = applicationCore.getGlobalComponent(UserId.class);
        agentContainer.acceptNewAgent(createAgentName(userId),
                                      new NotificationGuiAgent(userId, notificationManager)).start();
    }


    @Override
    public void start(AgentContainer container) throws Exception {
        this.agentContainer = container;
    }


    @Override
    public void stop() throws Exception {
        if (listenerController != null) {
            listenerController.kill();
            listenerController = null;
        }
    }


    public NotificationGuiPluginConfiguration getConfiguration() {
        return configuration;
    }


    public NotificationGuiOperations getOperations() {
        return operations;
    }


    private String createAgentName(UserId userId) {
        return "offline-agent-" + userId.getLogin() + userId.getObjectId() + userId.getLoginTime();
    }


    public class NotificationGuiPluginConfiguration {
        public void addNotificationHandler(String notificationId, NotificationHandler notificationHandler) {
            notificationManager.addNotificationHandler(notificationId, notificationHandler);
        }
    }

    private class NotifyJobListener extends JobEventHandler {
        NotifyJobListener() {
            super(JobRequestTemplate.matchType(NotificationRequest.NOTIFY_JOB_TYPE));
        }


        @Override
        protected void handleRequest(JobRequest request) {
            notificationManager.receive(new NotificationRequest(request));
        }
    }
}
