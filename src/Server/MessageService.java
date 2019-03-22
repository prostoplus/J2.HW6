package Server;

import java.io.IOException;

class MessageService {
    private final ClientStorage clientStorage;

    public MessageService(ClientStorage clientStorage) {
        this.clientStorage = clientStorage;
    }

    public synchronized void sendMessages(String message) {
        clientStorage.getClients().forEach(client -> {
            try {
                System.out.println(String.format("sending message '%s' to '%s'", message, client));
                client.getOs().writeUTF(message + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    synchronized void sendPrivateMessage(String login,String message) {
        try {
            Client receiver = clientStorage.findClient(login);
            if(receiver != null){
                receiver.getOs().writeUTF(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}