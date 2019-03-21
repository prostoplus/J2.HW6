package Server;

import java.io.IOException;

import Server.model.Client;

public class ClientServiceImpl implements ClientService {
    private final Client client;
    private final MessageService messageService;
    private final ClientStorage clientStorage;

    public ClientServiceImpl(Client client, MessageService messageService, ClientStorage clientStorage) {
        this.client = client;
        this.messageService = messageService;
        this.clientStorage = clientStorage;
    }

    @Override
    public void processMessage() {
        try {
            while (true) {
                String message = client.getIs().readUTF();
                System.out.println(String.format("received message '%s' to '%s'", message, client));

                messageService.sendMessages(client.getLogin() + "::" + message);
            }
        } catch (IOException io) {
            clientStorage.removeClient(client);
            io.printStackTrace();
        }
    }


}