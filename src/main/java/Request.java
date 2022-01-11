import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Request {
    private final String[] parts;

    public Request(BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();
        parts = requestLine.split(" ");
    }

    private String getPath() {
        return parts[1];
    }

    // параметры из Query String, согласно документации возвращает List<NameValuePair>
    public List<NameValuePair> getQueryParams() throws URISyntaxException {
        return URLEncodedUtils.parse(new URI(getPath()), StandardCharsets.UTF_8);
    }

    // поиск значения по ключу name
    public String getQueryParam(String name) throws URISyntaxException {
        List<NameValuePair> nameValuePairList = URLEncodedUtils.parse(new URI(getPath()), StandardCharsets.UTF_8);
        for (NameValuePair nameValuePair : nameValuePairList){
           String getValue = nameValuePair.getName();
           if(getValue.equals(name)){
               return nameValuePair.getValue();
           }
        }
        return "Not found";
    }

    public String[] getParts() {
        return parts;
    }

    // доработка функциональности поиска handler'а так, чтобы учитывался только путь без Query
    public String getPathWithoutQuery() throws MalformedURLException {
        URL url = new URL("https://example.com".concat(getPath()));
        return url.getPath();
    }
}
