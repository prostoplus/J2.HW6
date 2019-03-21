package Server.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Client {
    private final String login;
    private final DataInputStream is;
    private final DataOutputStream os;


    public Client(String login, DataInputStream is, DataOutputStream os) {
        this.login = login;
        this.is = is;
        this.os = os;
    }

    public String getLogin() {
        return login;
    }

    public DataInputStream getIs() {
        return is;
    }

    public DataOutputStream getOs() {
        return os;
    }

    @Override
    public String toString() {
        return "Client{" +
                "login='" + login + '\'' +
                '}';
    }
}