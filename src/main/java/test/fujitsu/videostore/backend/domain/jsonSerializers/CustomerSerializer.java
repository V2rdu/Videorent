package test.fujitsu.videostore.backend.domain.jsonSerializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import test.fujitsu.videostore.backend.domain.Customer;

import java.io.IOException;

public class CustomerSerializer extends JsonSerializer<Customer> {

    @Override
    public void serialize(Customer customer, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeNumber(customer.getId());
    }
}
