package test.fujitsu.videostore.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import test.fujitsu.videostore.backend.domain.jsonDeserializers.MovieTypeDeserializer;
import test.fujitsu.videostore.backend.domain.jsonSerializers.MovieTypeSerializer;

/**
 * Movie domain object
 */
@JsonPropertyOrder({"id", "name", "stockCount", "type"})
public class Movie {

    /**
     * Movie ID
     */
    private int id = -1;

    /**
     * Movie name
     */
    private String name;

    /**
     * Movies in stock
     */
    private int stockCount = 0;

    /**
     * Movie type
     */
    @JsonDeserialize(using = MovieTypeDeserializer.class)
    @JsonSerialize(using = MovieTypeSerializer.class)
    private MovieType type = MovieType.NEW;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStockCount() {
        return stockCount;
    }

    public void setStockCount(int stockCount) {
        this.stockCount = stockCount;
    }

    public MovieType getType() {
        return type;
    }

    public void setType(MovieType type) {
        this.type = type;
    }

    /**
     * New object for database or not
     *
     * @return boolean
     */
    @JsonIgnore
    public boolean isNewObject() {
        return id == -1;
    }
}