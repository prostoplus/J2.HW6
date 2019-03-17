import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ChatServer {

    public static void main(String[] args) throws IOException {

        ServerSocket server = null;
        Socket socket = null;

        try {
            server = new ServerSocket(8080);
            System.out.println("Server started");
            socket = server.accept();
            System.out.println("Client connected: " + socket);

            Scanner in =  new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner sc = new Scanner(System.in);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        System.out.println("Server, write you message");
                        String msg = sc.nextLine();
                        System.out.println("The message was send");
                        out.println(msg);
                    }
                }
            }).start();

            while (true) {
                String msg = in.nextLine();
                if (msg.equals("/end")) break;
                System.out.println("Client: " + msg);
                //               out.flush(); // это автоматический/принудительный сброс буфера вывода
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                server.close();
                System.out.println("Server closed");
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
