
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {
    private final int PORT = 9999;
    private final BlockingQueue<ClientHandler> clients = new LinkedBlockingQueue<>();

    public void start() {
        ExecutorService pool = Executors.newFixedThreadPool(64);
        try (final var serverSocket = new ServerSocket(PORT)) {
            while (true) {
                final var socket = serverSocket.accept();
                ClientHandler client = new ClientHandler(socket);
                clients.add(client);
                pool.execute(client);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }
}