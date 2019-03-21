package Server;

import java.io.IOException;

public interface ClientService {
    /**
     * Читает от клиента и рассылает
     */
    void processMessage() throws IOException;
}