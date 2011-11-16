package net.codjo.notification.gui.api;
import net.codjo.notification.common.message.NotificationRequest;
/**
 * Traite une notification particulière.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
public interface NotificationHandler {

    public void handle(NotificationRequest notification);
}
