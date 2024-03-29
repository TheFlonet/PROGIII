package progiii.common.data;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * Gestisce in modo atomico l’aumento del contatore per gli id
 * L’adapter implementa I metodi per scrivere e leggere informazioni json
 */
public class IdHandler implements Serializable {
    private final AtomicInteger idCounter;

    public IdHandler() {
        this.idCounter = new AtomicInteger();
    }

    public IdHandler(int startValue) {
        this.idCounter = new AtomicInteger(startValue);
    }

    public int getAndIncrement() {
        return idCounter.getAndIncrement();
    }

    public static class Adapter extends TypeAdapter<IdHandler> {
        @Override
        public void write(JsonWriter jsonWriter, IdHandler idHandler) throws IOException {
            jsonWriter.beginObject();
            jsonWriter.name("idHandler");
            jsonWriter.value(idHandler.idCounter);
            jsonWriter.endObject();
        }

        @Override
        public IdHandler read(JsonReader jsonReader) throws IOException {
            jsonReader.beginObject();
            jsonReader.nextName();
            IdHandler container = new IdHandler(jsonReader.nextInt());
            jsonReader.endObject();
            return container;
        }
    }
}
