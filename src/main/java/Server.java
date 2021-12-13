
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {


    private final int N_THREAD;

    public Server(int n_THREAD) {
        N_THREAD = n_THREAD;
    }

    public void start(int port) {
        ExecutorService pool = Executors.newFixedThreadPool(N_THREAD);

        try (final var serverSocket = new ServerSocket(port)) {
            while (true) {
                final var socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                pool.execute(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }
}

