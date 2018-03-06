package main;

import main.dispatch.JMSMessageDispatcher;
import main.dispatch.JMSMessageDispatcherFactory;
import main.log.LogAppenderFactory;
import main.log.LogWindow;
import main.model.InteractiveForm;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Formatter;

public class JMSUtility extends Component {
    private JPanel panel1;
    protected JTextField jmsServerUrl;
    protected JTextField queueDestinationTextField;
    protected JTextPane messageTextPane;
    protected JTextPane parameterListTextPane;
    private JButton sendMessageSButton;
    private JButton helpButton;
    private InteractiveForm messagePropertiesInteractiveForm;
    private JButton clearButton;
    JMSMessageDispatcher jmsMessageDispatcher;
    private final LogWindow logWindow = LogAppenderFactory.getLogWindow();
    private final JMSMessageDispatcherFactory jmsMessageDispatcherFactory = new JMSMessageDispatcherFactory();

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("JMS Utility");
        frame.setContentPane(new JMSUtility().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setAlwaysOnTop(false);
    }

    public JMSUtility() {
        helpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    showHelpDialog();
                } catch (Exception ex) {
                    showErrorPane(ex.getMessage(), ExceptionUtils.getFullStackTrace(ex));
                }
            }
        });
        sendMessageSButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    sendMessageActionPerformed();
                } catch (Exception ex) {
                    showErrorPane(ex.getMessage(), ExceptionUtils.getFullStackTrace(ex));
                }
            }
        });
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                messagePropertiesInteractiveForm.clear();
            }
        });
    }

    void sendMessageActionPerformed() {
        if (validateInputFields()) {
            allocateJMSMessageDispatcher();
            assembleAndDispatchMessages();
        }
    }

    void allocateJMSMessageDispatcher() {
        jmsMessageDispatcher = jmsMessageDispatcherFactory.getJMSMessageDispatcher(jmsServerUrl.getText());
    }

    boolean validateInputFields() {
        boolean isValid = true;
        if (!jmsServerUrl.getText().matches("\\w{2,5}://.*:\\d{2,6}")) {
            isValid = false;
            showErrorPane("input validation error", "JMS Server URL is not a valid URL. " +
                    "\n\n should be: [protocol]://[host]:[port]" +
                    "\n example:   tcp://localhost:61616");
        }
        if (StringUtils.isBlank(queueDestinationTextField.getText())) {
            isValid = false;
            showErrorPane("input validation error", "You must supply a Queue destination");
        }
        if (StringUtils.isBlank(messageTextPane.getText())) {
            isValid = false;
            showErrorPane("input validation error", "Message can not be blank");
        }
        int parameters = numberOfParametersInText(messageTextPane.getText());
        String[] parameterValues = getPramaterValuesArraySplitOnNewLineForNixAndWindows();
        if (parameters > 0 && parameterValues.length == 0) {
            isValid = false;
            showErrorPane("input validation error", "You have supplied a parameterized message but no parameters. " +
                    "\n\nPlease see the Help! dialog.");
        }
        if (parameters == 0 && parameterValues.length > 0) {
            isValid = false;
            showErrorPane("input validation error", "You have supplied parameters to a message that has no placeholders." +
                    "\n\nPlease see the Help! dialog.");
        }
        if (parameters > 1) {
            boolean parameterNumberOfFieldsMismatch = false;
            for (String parameterValue : parameterValues) {
                int numberOfFieldsInParameterValue = StringUtils.countMatches(parameterValue, ":");
                if (numberOfFieldsInParameterValue != (parameters - 1)) {
                    parameterNumberOfFieldsMismatch = true;
                }
            }
            if (parameterNumberOfFieldsMismatch) {
                isValid = false;
                showErrorPane("input validation error", "Your parameter list contains at least one entry that does not " +
                        "satisfy the parameterized message." +
                        "\n\nPlease see the Help! dialog.");
            }
        }
        return isValid;
    }

    private String[] getPramaterValuesArraySplitOnNewLineForNixAndWindows() {
        String text = parameterListTextPane.getText();
        text = text.replace("\r\n", "\n");
        return StringUtils.split(text, "\n");
    }

    int numberOfParametersInText(String text) {
        return StringUtils.countMatches(text, "%s");
    }

    void assembleAndDispatchMessages() {
        String destinationQueue = queueDestinationTextField.getText();
        String parameterizedMessagePayload = messageTextPane.getText();

        String[] parameterValues = getPramaterValuesArraySplitOnNewLineForNixAndWindows();
        if (parameterValues.length > 0) {
            logWindow.log("Sending messages for parameters in list. \nTotal number of messages to send=" + parameterValues.length);

            for (String parameterValue1 : parameterValues) {
                Formatter formatter = new Formatter();
                String[] values = parameterValue1.split(":");
                String parsedMessagePayload;
                if (values.length > 0) {
                    parsedMessagePayload = formatter.format(parameterizedMessagePayload, values).toString();
                } else {
                    parsedMessagePayload = formatter.format(parameterizedMessagePayload, parameterValue1).toString();
                }
                jmsMessageDispatcher.sendMessage(destinationQueue, parsedMessagePayload, messagePropertiesInteractiveForm.getMessageProperties());
            }
        } else {
            logWindow.log("Sending 1 message");
            jmsMessageDispatcher.sendMessage(destinationQueue, parameterizedMessagePayload, messagePropertiesInteractiveForm.getMessageProperties());
        }
    }

    private void showHelpDialog() {
        String message = "This tool let's you send a JMS messsage to a queue. " +
                "\nSupply the input fields with correct values and click send button." +
                "\n\nThere is a special feature for the 'message properties' and 'parameter list' input fields." +
                "\nThese fields work together, so if you supply the message with a placeholder, namely %s, " +
                "\nthen the tool will attempt to fill the placeholder with values from the parameter list. If you have one %s in you message, " +
                "\nthen you can send several messages with each message being given a new value for %s with the next value in the list. " +
                "\nThe parameter list entries need to be separated by a carriage return/new line feed. " +
                "\n------" +
                "\nExample parameter list for input with one %s:" +
                "\nparam1" +
                "\nparam2" +
                "\nparam3" +
                "\n------" +
                "\nIf you have more than one placeholder, then the values should be separated by a colon (:) in the parameter list. " +
                "\nSo message 'hello %s %s' could be given the parameter list 'mr:duke' and the resulting message would be 'hello mr duke'." +
                "\n------" +
                "\nExample parameter list for input with two %s:" +
                "\nmr:duke" +
                "\nms:daisy" +
                "\nmrs:robinson" +
                "\n------";
        JOptionPane pane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = pane.createDialog(this, "Help!");
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }

    void showErrorPane(String title, String msg) {
        logWindow.log(msg);
        JOptionPane pane = new JOptionPane(msg, JOptionPane.ERROR_MESSAGE);
        JDialog dialog = pane.createDialog("Application says: " + title);
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }
}
