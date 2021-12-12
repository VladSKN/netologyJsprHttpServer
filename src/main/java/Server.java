
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final int PORT;
    private final int N_THREAD;

    public Server(int PORT, int N_THREAD) {
        this.N_THREAD = N_THREAD;
        this.PORT = PORT;
    }

    public void start() {
        ExecutorService pool = Executors.newFixedThreadPool(N_THREAD);
        try {
                ClientHandler client = new ClientHandler(PORT);
                pool.execute(client);
        } finally {
            pool.shutdown();
        }
    }
}