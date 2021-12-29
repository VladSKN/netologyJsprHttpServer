import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class ClientHandler implements Runnable {

    private final List<String> VALID_PATH = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css",
            "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
    private final Socket socket;


    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                socket;
                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final var out = new BufferedOutputStream(socket.getOutputStream());
        ) {
            // read only request line for simplicity
            // must be in form GET /path HTTP/1.1
            final var requestLine = in.readLine();
            final var parts = requestLine.split(" ");

            if (parts.length != 3) {
                // just close socket
                return;
            }

            // доработка функциональности поиска handler'а так, чтобы учитывался только путь без Query
            final var pathWithoutQuery = parts[1].substring(0, parts[1].indexOf("?"));

            System.out.println(getQueryParams(parts[1]));

            if (!VALID_PATH.contains(pathWithoutQuery)) {
                out.write(errorMessage().getBytes());
                out.flush();
                return;
            }

            final var filePath = Path.of(".", "public", pathWithoutQuery);
            final var mimeType = Files.probeContentType(filePath);

            // special case for classic
            if (pathWithoutQuery.equals("/classic.html".substring(0, parts[1].indexOf("?")))) {
                final var template = Files.readString(filePath);
                final var content = template.replace(
                        "{time}",
                        LocalDateTime.now().toString()
                ).getBytes();
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + content.length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.write(content);
                out.flush();
                return;
            }

            final var length = Files.size(filePath);
            out.write(okMassage(mimeType, length).getBytes());
            Files.copy(filePath, out);
            out.flush();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    // параметры из Query String, согласно документации возвращает List<NameValuePair>
    private static List<NameValuePair> getQueryParams(String uri) throws URISyntaxException {
        return URLEncodedUtils.parse(new URI(uri), StandardCharsets.UTF_8);
    }

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

