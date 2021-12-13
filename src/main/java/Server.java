
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {

    private final int PORT;
    private final int N_THREAD;
    private final Map<String, Map<String, Handler>> handlers;
    private final List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html",
            "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");

    public Server(int PORT, int N_THREAD) {
        this.N_THREAD = N_THREAD;
        this.PORT = PORT;
        this.handlers = new ConcurrentHashMap<>();
    }

    public void addHandler(String method, String path, Handler handler) {
        if (this.handlers.get(method) == null) {
            this.handlers.put(method, new ConcurrentHashMap<>());
        }
        this.handlers.get(method).put(path, handler);
    }

    public void start() {
        ExecutorService pool = Executors.newFixedThreadPool(N_THREAD);
        try {
            Runnable runnable = () -> {
                try (final var serverSocket = new ServerSocket(PORT)) {
                    while (true) {
                        try (final var socket = serverSocket.accept();
                             final var in = socket.getInputStream();
                             final var out = new BufferedOutputStream(socket.getOutputStream());
                        ) {

                            Request request = Request.fromInputStream(in);
                            Map<String, Handler> handlerMap = handlers.get(request.getPath());
                            if (handlerMap == null) {
                                //TODO 404 error
                                return;
                            }

                            Handler handler = handlerMap.get(request.getPath());
                            if (handler == null) {
                                //TODO 404 error
                                return;
                            }
                            handler.handle(request, out);

//                    final var path = request.getPath();
//                    if (!validPaths.contains(path)) {
//                        out.write((errorMessage()).getBytes());
//                        out.flush();
//                        continue;
//                    }
//
//                    final var filePath = Path.of(".", "public", path);
//                    final var mimeType = Files.probeContentType(filePath);
//
//                    // special case for classic
//                    if (path.equals("/classic.html")) {
//                        final var template = Files.readString(filePath);
//                        final var content = template.replace(
//                                "{time}",
//                                LocalDateTime.now().toString()
//                        ).getBytes();
//                        out.write((
//                                "HTTP/1.1 200 OK\r\n" +
//                                        "Content-Type: " + mimeType + "\r\n" +
//                                        "Content-Length: " + content.length + "\r\n" +
//                                        "Connection: close\r\n" +
//                                        "\r\n"
//                        ).getBytes());
//                        out.write(content);
//                        out.flush();
//                        continue;
//                    }
//
//                    final var length = Files.size(filePath);
//                    out.write(okMassage(mimeType, length).getBytes());
//                    Files.copy(filePath, out);
//                    out.flush();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            pool.execute(runnable);
        } finally {
            pool.shutdown();
        }
    }
}