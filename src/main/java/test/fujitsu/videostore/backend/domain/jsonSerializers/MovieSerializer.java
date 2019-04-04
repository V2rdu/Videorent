package test.fujitsu.videostore.backend.domain.jsonSerializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import test.fujitsu.videostore.backend.domain.Movie;

import java.io.IOException;

public class MovieSerializer extends JsonSerializer<Movie> {

    @Override
    public void serialize(Movie movie, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeNumber(movie.getId());
    }
}
