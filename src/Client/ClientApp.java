package Client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class ClientApp extends JFrame {

    private Socket socket;
    private JTextArea outputTextArea;
    private JTextField inputTextField;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    public ClientApp() {
        initGui();
    }

    private void initReceiver() {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    String echoMessage = inputStream.readUTF();
                    System.out.println("Received message::" + echoMessage);
                    outputTextArea.append(echoMessage);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
        System.out.println("Receiver started");
    }

    private void initConnection() {
        try {
            socket = new Socket("Localhost", 8080);
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
            System.out.println("Connection initialized");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processMessage() {
        if (!inputTextField.getText().equals("")) {
            String message = inputTextField.getText();
            inputTextField.setText("");
            sendMessage(message);
        }

    }

    private void sendMessage(String message) {
        try {
            System.out.println("Sent message::" + message);
            outputStream.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initGui() {
        outputTextArea = new JTextArea();
        inputTextField = new JTextField();

        setTitle("client.ClientApp");
        setBounds(500, 200, 700, 700);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel textPanel = createTextPanel();
        JPanel buttonPanel = createButtonPanel();
        JPanel authPanel = createAuthPanel(textPanel, buttonPanel);

        add(textPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(authPanel, BorderLayout.NORTH);

        setVisible(true);

        System.out.println("GUI initialized ");
    }

    private JPanel createTextPanel() {
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BorderLayout());


        textPanel.add(new JScrollPane(outputTextArea));
        textPanel.setVisible(false);

        outputTextArea.setBackground(new Color(51, 153, 255));
        outputTextArea.setEditable(false);     //чтобы нельзя было печатать текст в поле
        return textPanel;


    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        inputTextField.setBackground(new Color(255, 204, 51));
        inputTextField.addActionListener(e -> processMessage());

        JButton button = new JButton("Send");

        buttonPanel.add(inputTextField);
        buttonPanel.add(button);
        buttonPanel.setVisible(false);
        //нажатие кнопки
        button.addActionListener(e -> {
            processMessage();
        });
        return buttonPanel;
    }

    private JPanel createAuthPanel(JPanel textPanel, JPanel buttonPanel) {
        JPanel authPanel = new JPanel();
        JTextField loginField = new JTextField();
        loginField.addActionListener(e -> processMessage());

        JButton authButton = new JButton("Auth");
        authButton.addActionListener(e -> {
            initConnection();
            initReceiver();
            sendMessage(loginField.getText());
            authPanel.setVisible(false);
            buttonPanel.setVisible(true);
            textPanel.setVisible(true);
        });


        authPanel.add(loginField);
        authPanel.add(authButton);
        authPanel.setLayout(new BoxLayout(authPanel, BoxLayout.X_AXIS));
        authPanel.setVisible(true);
        return authPanel;
    }

    public static void main(String[] args) {
        new ClientApp();
        new ClientApp();
    }
}