package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

class ChatServer2 {
    private static ClientsDB clientsDB = new ClientsDB();
    private static ClientStorage clientStorage = new ClientStorage();
    private static MessageService messageService = new MessageService(clientStorage);

    public static void main(String[] args) throws IOException {

        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("server started");
            while (true) {
                Socket socket = serverSocket.accept();

                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                String[] data = inputStream.readUTF().split("&");
                System.out.println("New" + data[0]);
                if (data[0].equals("login")) {
                    if (clientsDB.isClientNotInDB(data[1])) {
                        clientsDB.addToDB(data[1], data[2]);
                        Client client = createClient(data,inputStream,outputStream,socket);
                        System.out.println("New client connected:" + client + "::" + socket);
                    } else {
                        if (clientsDB.checkAuth(data[1], data[2])) {
                            Client client = createClient(data,inputStream,outputStream,socket);
                            System.out.println("Old client connected:" + client + "::" + socket);
                        } else {
                            System.out.println("Attempt to login failed for " + data[1] + " " + data[2]);
                            outputStream.writeUTF("fail");
                            startLoginAfterFailureThread(socket, inputStream, outputStream);
                        }
                    }
                }
            }
        }
    }

    private static Client createClient(
            String[] data,
            DataInputStream inputStream,
            DataOutputStream outputStream,
            Socket socket) throws IOException{
        Client client = new Client(data[1], inputStream, outputStream);
        clientStorage.addClient(client);
        outputStream.writeUTF("logged");
        ClientServiceImpl clientService = new ClientServiceImpl(client, messageService, clientStorage);
        clientService.processMessage(data[1] + " enters chat.");
        startListenThread(client,socket, clientService);
        return client;
    }

    private static void startListenThread(Client client, Socket socket,ClientServiceImpl clientService){
        Thread logoutThread = new Thread(() -> {
            while (true) {
                try {
                    listenToInputStream(client, socket,clientService);
                }catch (SocketException e) {
                    e.printStackTrace();
                    System.out.println("Listener failed");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        logoutThread.setDaemon(true);
        logoutThread.start();
    }

    private static void startLoginAfterFailureThread(Socket socket,DataInputStream inputStream,DataOutputStream outputStream){
        Thread loginAfter = new Thread(() -> {
            while (true) {
                try {
                    if(loginAfterFailure(socket,inputStream,outputStream)) return;
                } catch (IOException e) {
                    System.out.println("Failed after failure");
                    e.printStackTrace();
                }
            }
        });
        loginAfter.setDaemon(true);
        loginAfter.start();
    }

    private static boolean loginAfterFailure(Socket socket,DataInputStream inputStream,DataOutputStream outputStream) throws IOException{
        String[] data = inputStream.readUTF().split("&");
        System.out.println("After failure " + data[0]);
        if (data[0].equals("login")) {
            if (clientsDB.isClientNotInDB(data[1])) {
                clientsDB.addToDB(data[1], data[2]);
                Client client = createClient(data,inputStream,outputStream,socket);
                System.out.println("New client connected:" + client + "::" + socket);
                return true;
            } else {
                if (clientsDB.checkAuth(data[1], data[2])) {
                    Client client = createClient(data,inputStream,outputStream,socket);
                    System.out.println("Old client connected:" + client + "::" + socket);
                    return true;
                } else {
                    if(clientStorage.containsClient(data[1])){
                        System.out.println(data[1] + " is already logged in.");
                        outputStream.writeUTF("occupied");
                    }else {
                        System.out.println("Attempt to login failed for " + data[1]);
                        outputStream.writeUTF("fail");
                    }
                }
            }
        }
        return false;
    }

    private static void listenToInputStream (Client client, Socket socket, ClientServiceImpl clientService) throws IOException {
        if (!socket.isClosed()) {
            String[] data = client.getIs().readUTF().split("&");
            System.out.println("listen " + data[0]);
            switch (data[0]) {
                case "":
                    System.out.println("Received message");
                    clientService.processMessage(data[1]);
                    break;
                case "pm":
                    System.out.println("Received private message");
                    clientService.processPrivateMessage(data[1],data[2]);
            }
        }
    }
}