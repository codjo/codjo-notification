/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.notification.gui.plugin;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.notification.common.message.NotificationRequest;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import junit.framework.TestCase;
/**
 *
 */
public class NotificationManagerTest extends TestCase {
    private NotificationManager manager;
    private DefaultGuiContext defaultGuiContext;


    public void test_interpretMessage_unknownTaskId() throws Exception {
        NotificationRequest notificationRequest =
              new NotificationRequest("unknownTaskId");

        manager.setDefaultNotificationHandler(new DefaultNotificationHandler(getOperations()));
        manager.getListener().receive(notificationRequest);

        assertEquals(1, defaultGuiContext.getDesktopPane().getAllFrames().length);
        JInternalFrame frame = defaultGuiContext.getDesktopPane().getAllFrames()[0];
        assertEquals("Message du serveur inconnu", frame.getTitle());
    }


    public void test_interpretMessage_welcome() throws Exception {
        NotificationRequest notificationRequest =
              new NotificationRequest("displayWelcomeMessage");

        manager.addNotificationHandler("displayWelcomeMessage",
                                       new WelcomeNotificationHandler(getOperations()));
        manager.getListener().receive(notificationRequest);

        assertEquals(1, defaultGuiContext.getDesktopPane().getAllFrames().length);
        JInternalFrame frame = defaultGuiContext.getDesktopPane().getAllFrames()[0];

        Thread.sleep(30);

        assertEquals("Bienvenue !", frame.getTitle());
    }


    @Override
    protected void setUp() throws Exception {
        defaultGuiContext = new DefaultGuiContext(new JDesktopPane());
        manager = new NotificationManager();
    }


    private NotificationGuiOperationsImpl getOperations() {
        NotificationGuiOperationsImpl operations = new NotificationGuiOperationsImpl();
        operations.setGuiContext(defaultGuiContext);
        return operations;
    }
}
