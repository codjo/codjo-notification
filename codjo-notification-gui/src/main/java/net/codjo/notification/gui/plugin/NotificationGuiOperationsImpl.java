package net.codjo.notification.gui.plugin;
import net.codjo.gui.toolkit.util.GuiUtil;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.notification.gui.api.MessageFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
/**
 *
 */
class NotificationGuiOperationsImpl implements NotificationGuiOperations {
    private MessageFrame messageFrame;
    private final Object lock = new Object();
    private GuiContext guiContext;


    public void displayNotification(String title, String message) {
        getMessageFrame().postMessage(title, message);
    }


    public MessageFrame getMessageFrame() {
        synchronized (lock) {
            if (messageFrame == null) {
                createAndDisplayMessageFrame();
            }
            return messageFrame;
        }
    }


    public void setGuiContext(GuiContext guiContext) {
        this.guiContext = guiContext;
    }


    private void createAndDisplayMessageFrame() {
        messageFrame = new MessageFrame();
        messageFrame.setTitle("Système de notification");
        messageFrame.pack();
        guiContext.getDesktopPane().add(messageFrame);
        GuiUtil.centerWindow(messageFrame);
        messageFrame.setVisible(true);
        messageFrame.addInternalFrameListener(new MessageFrameCleaner());
    }


    private class MessageFrameCleaner extends InternalFrameAdapter {
        @Override
        public void internalFrameClosing(InternalFrameEvent event) {
            cleanField();
        }


        @Override
        public void internalFrameClosed(InternalFrameEvent event) {
            cleanField();
        }


        private void cleanField() {
            synchronized (lock) {
                messageFrame = null;
            }
        }
    }
}
