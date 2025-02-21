import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

    public class ContactClientGUI extends JFrame {
        private static final String SERVER_ADDRESS = "localhost";
        private static final int SERVER_PORT = 12345;

        private final JTextField nameField = new JTextField(20);
        private final JTextField phoneField = new JTextField(20);
        private final JTextField emailField = new JTextField(20);
        private final JTextField companyField = new JTextField(20);
        private final JTextField commentField = new JTextField(20);
        private final JTextField searchField = new JTextField(20);
        private final JTextArea contactList = new JTextArea(20, 50);
        private PrintWriter out;
        private Socket socket;

        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                try {
                    new ContactClientGUI().setVisible(true);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null,
                            "Error connecting to server: " + e.getMessage(),
                            "Connection Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });
        }

        public ContactClientGUI() throws IOException {
            connectToServer();
            setupGUI();
            startMessageListener();
        }

        private void connectToServer() throws IOException {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
        }

        private void setupGUI() {
            setTitle("Contact Information System");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout(5, 5));

            // Input Panel
            JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
            inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            inputPanel.add(new JLabel("Name:"));
            inputPanel.add(nameField);
            inputPanel.add(new JLabel("Phone:"));
            inputPanel.add(phoneField);
            inputPanel.add(new JLabel("Email:"));
            inputPanel.add(emailField);
            inputPanel.add(new JLabel("Company:"));
            inputPanel.add(companyField);
            inputPanel.add(new JLabel("Comment:"));
            inputPanel.add(commentField);
            inputPanel.add(new JLabel("Search:"));
            inputPanel.add(searchField);

            // Button Panel
            JPanel buttonPanel = new JPanel();
            JButton addButton = new JButton("Add Contact");
            JButton clearButton = new JButton("Clear Fields");
            JButton refreshButton = new JButton("Refresh");
            JButton searchButton = new JButton("Search");

            buttonPanel.add(addButton);
            buttonPanel.add(clearButton);
            buttonPanel.add(refreshButton);
            buttonPanel.add(searchButton);

            // Contact List Panel
            contactList.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(contactList);
            scrollPane.setBorder(BorderFactory.createTitledBorder("Contacts"));

            // Add Components to Frame
            add(inputPanel, BorderLayout.NORTH);
            add(buttonPanel, BorderLayout.CENTER);
            add(scrollPane, BorderLayout.SOUTH);

            // Add Listeners
            addButton.addActionListener(e -> addContact());
            clearButton.addActionListener(e -> clearFields());
            refreshButton.addActionListener(e -> refreshContacts());
            searchButton.addActionListener(e -> searchContacts());
            searchField.addActionListener(e -> searchContacts());

            pack();
            setLocationRelativeTo(null);
            refreshContacts();
        }

        private void startMessageListener() {
            new Thread(() -> {
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()))) {
                    String messageStr;
                    while ((messageStr = in.readLine()) != null) {
                        Message message = Message.fromString(messageStr);
                        handleServerMessage(message);
                    }
                } catch (IOException e) {
                    showError("Lost connection to server: " + e.getMessage());
                }
            }).start();
        }

        private void handleServerMessage(Message message) {
            SwingUtilities.invokeLater(() -> {
                switch (message.getType()) {
                    case RESPONSE -> contactList.setText(message.getContent());
                    case ERROR -> showError(message.getContent());
                }
            });
        }

        private void addContact() {
            String contactData = String.join(":",
                    nameField.getText(),
                    phoneField.getText(),
                    emailField.getText(),
                    commentField.getText(),
                    companyField.getText());
            sendMessage(new Message(Message.Type.ADD_CONTACT, contactData));
            clearFields();
        }

        private void refreshContacts() {
            sendMessage(new Message(Message.Type.GET_CONTACTS, ""));
        }

        private void searchContacts() {
            String query = searchField.getText().trim();
            if (!query.isEmpty()) {
                sendMessage(new Message(Message.Type.SEARCH_CONTACTS, query));
            } else {
                refreshContacts();
            }
        }

        private void clearFields() {
            nameField.setText("");
            phoneField.setText("");
            emailField.setText("");
            companyField.setText("");
            commentField.setText("");
        }

        private void sendMessage(Message message) {
            out.println(message.toString());
        }

        private void showError(String error) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
        }
}
