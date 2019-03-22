package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    private static ClientsDB clientsDB = new ClientsDB();
    private static ClientStorage clientStorage = new ClientStorage();
    private static MessageService messageService = new MessageService(clientStorage);

    public static void main(String[] args) throws IOException {

        try (ServerSocket ss = new ServerSocket(8080)) {
            System.out.println("server started");
            while (true) {
                Socket socket = ss.accept();
                DataInputStream is = new DataInputStream(socket.getInputStream());
                DataOutputStream os = new DataOutputStream(socket.getOutputStream());

                Client client = new Client(is.readUTF(), is, os);
                System.out.println("client connected::" + client + "::" + socket);
                clientStorage.addClient(client);
                new Thread(() -> new ClientServiceImpl(client, messageService, clientStorage)
                        .processMessage()).start();
            }
        }
    }

    private static void listenToInputStream (Client client, Socket socket, ClientService clientService) throws IOException {
        if (!socket.isClosed()) {
            String[] data = client.getIs().readUTF().split("&");
            System.out.println("listen " + data[0]);
            switch (data[0]) {
                case "login":
                    if (clientsDB.checkAuth(data[1], data[2])) {
                        System.out.println("Client connected again: " + client + "::" + socket);
                        clientStorage.addClient(client);
                        client.getIs().writeUTF("logged");
                        clientService.processMessage(data[1] + " enters chat.");
                    } else {
                        System.out.println("Attempt to login failed for " + data[1] + " " + data[2]);
                        client.getOs().writeUTF("fail");
                    }
                    break;
                case "logout":
                    System.out.println("Client disconnected:" + client + "::" + socket);
                    client.getOs().writeUTF("logout");
                    clientStorage.removeClient(client);
                    clientService.processMessage(data[1] + " leaves chat.");
                    break;
                case "logoutFull":
                    System.out.println("Client is off.");
                    try {
                        System.out.println("Socket for " + client + " is off");
                        clientService.processMessage(data[1] + " leaves chat.");
                        socket.close();
                    } catch (IOException e) {
                        System.out.println("Unable to close socket");
                        e.printStackTrace();
                    }
                    break;
                case "message":
                    System.out.println("Received message");
                    clientService.processMessage(data[1]);
                    break;
                case "pm":
                    System.out.println("Received private message");
                    ClientServiceImpl.processPrivateMessage(data[1],data[2]);
            }
        }
    }
}