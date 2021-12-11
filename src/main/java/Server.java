
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int PORT;
    private final int N_THREAD = 64;

    public Server(int PORT) {
        this.PORT = PORT;
    }

    public void start() {
        ExecutorService pool = Executors.newFixedThreadPool(N_THREAD);
        try (final var serverSocket = new ServerSocket(PORT)) {
            while (true) {
                final var socket = serverSocket.accept();
                ClientHandler client = new ClientHandler(socket);
                pool.execute(client);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }
}