package Server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

class ClientServiceImpl implements ClientService {
    private final Client client;
    private final MessageService messageService;
    private final ClientStorage clientStorage;

    ClientServiceImpl(Client client, MessageService messageService, ClientStorage clientStorage) {
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

    void processPrivateMessage(String login,String message) {
        if(clientStorage.containsClient(login)) {
            System.out.println(String.format("received message '%s' to '%s'", message, client));
            Message processedMessage = new Message(message);
            message = "message&" + client.getLogin() + " to " + login + ": " +
                    processedMessage.splitMessage() + "&" +
                    processedMessage.getTime();

            messageService.sendPrivateMessage(login, message);
        }
    }

    private class Message{
        private String message;
        private final int width = 45;
        private int rowsCount = 1;

        Message(String message){
            this.message = message;
        }

        private String splitMessage(){
            StringBuilder splitMessage = new StringBuilder();
            int lineLength = message.length();
            int wordsLength = 0;
            if(lineLength >= width){
                String[] words = message.split(" ");
                for(String word : words){
                    wordsLength += word.length() + 1;
                    if(wordsLength > width){
                        splitMessage.append("\n").append(word).append(" ");
                        wordsLength = word.length() + 1;
                        rowsCount++;
                    }else{
                        splitMessage.append(word).append(" ");
                    }
                }
                splitMessage.append("\n");
                return splitMessage.toString();
            }else{
                return message + "\n";
            }
        }

        private String getTime(){
            StringBuilder time = new StringBuilder(new SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(Calendar.getInstance().getTime()));
            for(int i = 0; i < rowsCount; i++){
                time.append("\n");
            }
            return time.toString();
        }
    }

}