import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    // ссылка для отправки запроса
    public static final String URL = "https://api.nasa.gov/planetary/apod?api_key=gUW0BP1YOCZgdgzh2ibyQET5iOuVFuZYrGegKs8O";
    public static final ObjectMapper mapper = new ObjectMapper();

    //Настраиваем наш HTTP клиент, который будет отправлять запросы
    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();

        //Отправляем запрос и получаем ответ
        CloseableHttpResponse response = httpClient.execute(new HttpGet(URL));

        //Преобразуем ответ в Java-объект NasaObject
        NasaObject nasaObject = mapper.readValue(response.getEntity().getContent(), NasaObject.class);
        System.out.println(nasaObject);
        // на этом этапе можем получить JSON

        // Отправляем запрос и получаем ответ с нашей картинкой
        CloseableHttpResponse pictureResponse = httpClient.execute(new HttpGet(nasaObject.getUrl()));

        //Формируем автоматически название для файла
        String[] arr = nasaObject.getUrl().split("/");
        String file = arr[6];

        //Проверяем что наш ответ не null
        HttpEntity entity = pictureResponse.getEntity();
        if (entity != null) {
            //сохраняем в файл
            FileOutputStream fos = new FileOutputStream(file);
            entity.writeTo(fos);
            fos.close();
        }
    }
}
