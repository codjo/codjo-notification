/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.notification.gui.plugin;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import junit.framework.TestCase;
/**
 *
 */
public class WelcomeNotificationHandlerTest extends TestCase {
    private DefaultGuiContext context = null;
    private WelcomeNotificationHandler welcomeHandler = null;


    public void test_proceed() throws Exception {
        assertEquals(0, context.getDesktopPane().getAllFrames().length);
        welcomeHandler.handle(null);
        assertEquals(1, context.getDesktopPane().getAllFrames().length);
        JInternalFrame frame = context.getDesktopPane().getAllFrames()[0];

        waitGuiThread();

        assertEquals("Bienvenue !", frame.getTitle());
        assertTrue(frame.isVisible());

        final Rectangle bounds = frame.getBounds();
        assertTrue(bounds.width > 100);
        assertTrue(bounds.height > 100);
    }


    @Override
    protected void setUp() {
        context = new DefaultGuiContext(new JDesktopPane());
        NotificationGuiOperationsImpl operations = new NotificationGuiOperationsImpl();
        operations.setGuiContext(context);
        welcomeHandler = new WelcomeNotificationHandler(operations);
    }


    private void waitGuiThread() throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
            }
        });
    }


    public static void main(String[] args) {
        DefaultGuiContext guiContext = new DefaultGuiContext(new JDesktopPane());
        NotificationGuiOperationsImpl operations = new NotificationGuiOperationsImpl();
        operations.setGuiContext(guiContext);
        WelcomeNotificationHandler welcomeHandler = new WelcomeNotificationHandler(operations);

        JFrame frame = new JFrame();
        JPanel content = new JPanel(new BorderLayout());
        content.add(guiContext.getDesktopPane(), BorderLayout.CENTER);
        frame.setContentPane(content);
        frame.setSize(500, 500);
        frame.setVisible(true);

        welcomeHandler.handle(null);
    }
}
