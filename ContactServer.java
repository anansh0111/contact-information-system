import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ContactServer {
    private static final int PORT = 12345;
    private final ContactManager contactManager;
    private final ExecutorService executorService;
    private ServerSocket serverSocket;
    private volatile boolean running = true;

    public ContactServer() {
        this.contactManager = new ContactManager();
        this.executorService = Executors.newCachedThreadPool();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket, contactManager);
                executorService.execute(clientHandler);
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        running = false;
        executorService.shutdown();
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final ContactManager contactManager;
        private final PrintWriter out;
        private final BufferedReader in;

        public ClientHandler(Socket socket, ContactManager contactManager) throws IOException {
            this.clientSocket = socket;
            this.contactManager = contactManager;
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        @Override
        public void run() {
            try {
                String messageStr;
                while ((messageStr = in.readLine()) != null) {
                    Message message = Message.fromString(messageStr);
                    handleMessage(message);
                }
            } catch (IOException e) {
                System.err.println("Error handling client: " + e.getMessage());
            } finally {
                cleanup();
            }
        }

        private void handleMessage(Message message) {
            try {
                switch (message.getType()) {
                    case ADD_CONTACT -> {
                        String[] parts = message.getContent().split(":");
                        Contact contact = new Contact(parts[0], parts[1], parts[2], parts[3], parts[4]);
                        if (ContactValidator.isValid(contact)) {
                            contactManager.addContact(contact);
                            sendContactsList();
                        } else {
                            sendError("Invalid contact information");
                        }
                    }
                    case GET_CONTACTS -> sendContactsList();
                    case SEARCH_CONTACTS -> {
                        String query = message.getContent();
                        StringBuilder result = new StringBuilder();
                        for (Contact contact : contactManager.searchContacts(query)) {
                            result.append(contact.toString()).append("\n");
                        }
                        sendResponse(result.toString());
                    }
                }
            } catch (Exception e) {
                sendError("Error processing request: " + e.getMessage());
            }
        }

        private void sendContactsList() {
            StringBuilder result = new StringBuilder();
            for (Contact contact : contactManager.getAllContacts()) {
                result.append(contact.toString()).append("\n");
            }
            sendResponse(result.toString());
        }

        private void sendResponse(String content) {
            out.println(new Message(Message.Type.RESPONSE, content));
        }

        private void sendError(String error) {
            out.println(new Message(Message.Type.ERROR, error));
        }

        private void cleanup() {
            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error cleaning up client handler: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        ContactServer server = new ContactServer();
        server.start();
    }
}

