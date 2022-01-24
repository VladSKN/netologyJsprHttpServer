import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Request {
    private String[] parts;
    private List<NameValuePair> nameValuePairList;

    public Request(BufferedReader bufferedReader) throws IOException, URISyntaxException {
        init(bufferedReader);
    }

    private void init(BufferedReader bufferedReader) throws IOException, URISyntaxException {
        String requestLine = bufferedReader.readLine();
        parts = requestLine.split(" ");
        getQueryParams();
    }

    private String getPath() {
        return parts[1];
    }

    // параметры из Query String, согласно документации возвращает List<NameValuePair>
    private void getQueryParams() throws URISyntaxException {
        nameValuePairList = URLEncodedUtils.parse(new URI(getPath()), StandardCharsets.UTF_8);
    }

    public List<NameValuePair> getQueryParams1()  {
        return nameValuePairList;
    }

    // поиск значения по ключу name
    public List<String> getQueryParam(String name)  {
        List<String> queryParamList = new ArrayList<>();
        for (NameValuePair nameValuePair : nameValuePairList) {
            if (nameValuePair.getName().equals(name)) {
                queryParamList.add(nameValuePair.getValue());
            }
        }
        return queryParamList;
    }

    public String[] getParts() {
        return parts;
    }

    // доработка функциональности поиска handler'а так, чтобы учитывался только путь без Query
    public String getPathWithoutQuery() {
        int indexPath = getPath().lastIndexOf('?');
        return getPath().substring(0, indexPath);
    }
}
