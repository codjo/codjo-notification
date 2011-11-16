package net.codjo.notification.gui.api;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import static java.util.Collections.synchronizedList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
/**
 * Fenetre affichant des messages.
 */
public class MessageFrame extends JInternalFrame {
    private static final Logger APP = Logger.getLogger(MessageFrame.class);
    private JPanel contentPanel = new JPanel();
    private GridBagLayout gridBagLayout1 = new GridBagLayout();
    private JLabel iconLabel = new JLabel();
    private JScrollPane jScrollPane1 = new JScrollPane();
    private JTextPane messageTextPane = new JTextPane();
    private JButton okButton = new JButton();
    private JPanel buttonPanel = new JPanel();
    private FlowLayout buttonPanelLayout = new FlowLayout();
    private JButton nextMsgButton = new JButton();
    private JButton prevMsgButton = new JButton();
    private List<DisplayData> messagesList = synchronizedList(new ArrayList<DisplayData>());
    private int currentMsgIdx = 0;
    private JLabel infoLabel = new JLabel();


    public MessageFrame() {
        jbInit();
    }


    public void setMessage(String msg) {
        messageTextPane.setText(msg);
    }


    public void postMessage(String notificationTitle, String message) {
        messagesList.add(new DisplayData(notificationTitle, message));
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateGui();
            }
        });
    }


    void updateGui() {
        try {
            if (currentMsgIdx >= messagesList.size() || currentMsgIdx < 0) {
                currentMsgIdx = 0;
            }

            DisplayData current = messagesList.get(currentMsgIdx);
            setTitle(current.getTitle());
            messageTextPane.setName("[" + getTitle() + "].messageTextPane");
            messageTextPane.setText(current.getMessage());
            prevMsgButton.setEnabled(currentMsgIdx > 0);
            nextMsgButton.setEnabled((currentMsgIdx + 1) < messagesList.size());
            infoLabel.setText("message " + (currentMsgIdx + 1) + " sur " + messagesList.size());
            pack();
        }
        catch (Exception ex) {
            manageError(ex);
        }
    }


    private void validateFrame() {
        doDefaultCloseAction();
    }


    private void nextMessage() {
        currentMsgIdx++;
        updateGui();
    }


    private void previousMessage() {
        currentMsgIdx--;
        updateGui();
    }


    private void manageError(Exception ex) {
        String message =
              "Erreur lors de la lecture du message : " + messagesList.get(currentMsgIdx);
        APP.error(message, ex);
        messageTextPane.setText(message);
        messageTextPane.setForeground(Color.red);
        setTitle("ERREUR !!");
        prevMsgButton.setEnabled(false);
        nextMsgButton.setEnabled(false);
    }


    private void jbInit() {
        iconLabel.setIcon(new ImageIcon(MessageFrame.class.getResource("message.icon.png")));
        messageTextPane.setName("messageTextPane");
        messageTextPane.setContentType("text/html");
        messageTextPane.setEditable(false);
        this.setClosable(true);
        this.setResizable(true);
        this.setPreferredSize(new Dimension(550, 355));
        contentPanel.setLayout(gridBagLayout1);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setAlignment(FlowLayout.RIGHT);
        nextMsgButton.setEnabled(false);
        nextMsgButton.setToolTipText("Message suivant");
        nextMsgButton.setText(">>");
        nextMsgButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                nextMessage();
            }
        });
        prevMsgButton.setEnabled(false);
        prevMsgButton.setToolTipText("Message précédant");
        prevMsgButton.setText("<<");
        prevMsgButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                previousMessage();
            }
        });
        infoLabel.setFont(new Font("Serif", 0, 12));
        infoLabel.setText("message 1 sur 1");
        buttonPanel.add(prevMsgButton, null);
        buttonPanel.add(nextMsgButton, null);
        this.getContentPane().add(contentPanel);
        okButton.setText("Fermer");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                validateFrame();
            }
        });
        contentPanel.add(iconLabel,
                         new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                                GridBagConstraints.NONE, new Insets(5, 15, 17, 0), 10, -6));
        contentPanel.add(jScrollPane1,
                         new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 22), 0, 0));
        contentPanel.add(buttonPanel,
                         new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 17), 0, 0));
        buttonPanel.add(okButton, null);
        contentPanel.add(infoLabel,
                         new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 22), 0,
                                                0));
        jScrollPane1.getViewport().add(messageTextPane, null);
        getRootPane().setDefaultButton(okButton);
    }


    private static class DisplayData {
        private String title;
        private String message;


        DisplayData(String title, String message) {
            this.title = title;
            this.message = message;
        }


        public String getTitle() {
            return title;
        }


        public String getMessage() {
            return message;
        }
    }
}
