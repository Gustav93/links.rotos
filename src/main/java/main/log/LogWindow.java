package main.log;

import javax.swing.*;

public class LogWindow extends JFrame implements LogAppender {
    private final JTextArea textArea = new JTextArea();

    @SuppressWarnings({"UndesirableClassUsage"})
    LogWindow() {
        super("Log Window");
        setSize(500, 300);
        setLocation(575, 0);
        add(new JScrollPane(textArea));
        setVisible(true);
    }

    public void log(String msg) {
        System.out.println(msg);
        textArea.append(msg + "\n");
        this.validate();
    }
}