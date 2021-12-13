import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final ExecutorService executorService;
    private final Map<String, Map<String, Handler>> handlers;
    private final Handler handlerNotFound = ((request, bufferedOutputStream) -> {
        bufferedOutputStream.write((
                "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        bufferedOutputStream.flush();
    });



    public Server(int nThreads) {
        this.executorService = Executors.newFixedThreadPool(nThreads);
        this.handlers = new ConcurrentHashMap<>();
    }

    public void addHandler(String method, String path, Handler handler) {
        if (this.handlers.get(method) == null) {
            this.handlers.put(method, new ConcurrentHashMap<>());
        }
        this.handlers.get(method).put(path, handler);
    }

    public void start(int port) {
        try (final var serverSocket = new ServerSocket(port)) {
            while (true) {
                final var socket = serverSocket.accept();
                executorService.execute(() -> handleConnection(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

    private void handleConnection(Socket socket) {
        try (
                socket;
                final var in = (socket.getInputStream());
                final var out = new BufferedOutputStream(socket.getOutputStream());
        ) {

            Request request = Request.fromInputStream(in);
            Map<String, Handler> handlerMap = handlers.get(request.getMethod());
            if (handlerMap == null) {
                handlerNotFound.handle(request, out);
                return;
            }
            Handler handler = handlerMap.get(request.getPath());
            if (handler == null) {
                handlerNotFound.handle(request, out);
                return;
            }
            handler.handle(request, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

