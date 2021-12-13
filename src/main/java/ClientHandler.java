import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHandler {

    //private final int PORT;

//    public ClientHandler(int PORT) {
//        this.PORT = PORT;
//
//    }

//    @Override
//    public void run() {
//        final var validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html",
//                "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
//
//        try (final var serverSocket = new ServerSocket(PORT)) {
//            while (true) {
//                try (final var socket = serverSocket.accept();
//                     final var in = socket.getInputStream();
//                     final var out = new BufferedOutputStream(socket.getOutputStream());
//                ) {
//
//                    Request request = Request.fromInputStream(in);
//                    Map<String, Handler> handlerMap = handlers.get(request.getPath());
//                    if (handlerMap == null) {
//                        //TODO 404 error
//                        return;
//                    }
//
//                    Handler handler = handlerMap.get(request.getPath());
//                    if (handler == null) {
//                        //TODO 404 error
//                        return;
//                    }
//                    handler.handle(request, out);
//
////                    final var path = request.getPath();
////                    if (!validPaths.contains(path)) {
////                        out.write((errorMessage()).getBytes());
////                        out.flush();
////                        continue;
////                    }
////
////                    final var filePath = Path.of(".", "public", path);
////                    final var mimeType = Files.probeContentType(filePath);
////
////                    // special case for classic
////                    if (path.equals("/classic.html")) {
////                        final var template = Files.readString(filePath);
////                        final var content = template.replace(
////                                "{time}",
////                                LocalDateTime.now().toString()
////                        ).getBytes();
////                        out.write((
////                                "HTTP/1.1 200 OK\r\n" +
////                                        "Content-Type: " + mimeType + "\r\n" +
////                                        "Content-Length: " + content.length + "\r\n" +
////                                        "Connection: close\r\n" +
////                                        "\r\n"
////                        ).getBytes());
////                        out.write(content);
////                        out.flush();
////                        continue;
////                    }
////
////                    final var length = Files.size(filePath);
////                    out.write(okMassage(mimeType, length).getBytes());
////                    Files.copy(filePath, out);
////                    out.flush();
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private String errorMessage() {
        return "HTTP/1.1 404 Not Found\r\n" +
                "Content-Length: 0\r\n" +
                "Connection: close\r\n" +
                "\r\n";
    }

    private String okMassage(String mimeType, long length) {
        return "HTTP/1.1 200 OK\r\n" +
                "Content-Type: " + mimeType + "\r\n" +
                "Content-Length: " + length + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";
    }
}