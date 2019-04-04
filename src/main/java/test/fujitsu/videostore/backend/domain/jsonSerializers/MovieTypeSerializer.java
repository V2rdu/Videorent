package test.fujitsu.videostore.backend.domain.jsonSerializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import test.fujitsu.videostore.backend.domain.MovieType;

import java.io.IOException;

public class MovieTypeSerializer extends JsonSerializer<MovieType> {

    @Override
    public void serialize(MovieType movieType, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        switch (movieType) {
            case NEW:
                jsonGenerator.writeNumber(MovieType.NEW.getDatabaseId());
                break;
            case REGULAR:
                jsonGenerator.writeNumber(MovieType.REGULAR.getDatabaseId());
                break;
            case OLD:
                jsonGenerator.writeNumber(MovieType.OLD.getDatabaseId());
                break;
        }
    }
}
