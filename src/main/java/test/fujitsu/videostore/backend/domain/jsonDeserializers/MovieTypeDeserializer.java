package test.fujitsu.videostore.backend.domain.jsonDeserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import test.fujitsu.videostore.backend.domain.MovieType;

import java.io.IOException;

public class MovieTypeDeserializer extends JsonDeserializer<MovieType> {

    @Override
    public MovieType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        int type = Integer.parseInt(jsonParser.getText());
        MovieType movieType = MovieType.NEW;
        switch (type) {
            case 1:
                movieType = MovieType.NEW;
                break;
            case 2:
                movieType = MovieType.REGULAR;
                break;
            case 3:
                movieType = MovieType.OLD;
                break;
        }
        return movieType;
    }
}
