package test.fujitsu.videostore.backend.database;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import test.fujitsu.videostore.backend.domain.Customer;
import test.fujitsu.videostore.backend.domain.Movie;
import test.fujitsu.videostore.backend.domain.RentOrder;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.*;

/**
 * database Factory.
 * <p>
 */
public class DatabaseFactory {

    /**
     * Creates database "connection"/opens database from path.
     * <p>
     * Two example files, /db-examples/database.json and /db-examples/database.yaml.
     * Hint: MovieType.databaseId == type field in database files.
     *
     * @param filePath file path to database
     * @return database proxy for different tables
     */
    public static Database from(String filePath) {

        ObjectMapper jsonMapper = new ObjectMapper();
        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

        final File file = new File(filePath);
        JsonNode jsonNode = null;
        try {
            String content = FileUtils.readFileToString(file, "utf-8");
            if (filePath.endsWith(".yaml")) {
                Object obj = yamlMapper.readValue(content, Object.class);
                content = jsonMapper.writeValueAsString(obj);
            }
            jsonNode = jsonMapper.readTree(content);
        } catch (IOException e) {
        }

        final JsonNode[] finalJsonNode = {jsonNode};

        return new Database() {

            @Override
            public DBTableRepository<Movie> getMovieTable() {

                final List<Movie> movieList = new ArrayList<>();

                String movies = finalJsonNode[0].get("movie").toString();

                try {
                    Movie[] all = jsonMapper.readValue(movies, Movie[].class);
                    movieList.addAll(Arrays.asList(all));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return new DBTableRepository<Movie>() {

                    @Override
                    public List<Movie> getAll() {
                        return movieList;
                    }

                    @Override
                    public Movie findById(int id) {
                        return movieList.stream().filter(movie -> movie.getId() == id).findFirst().get();
                    }

                    @Override
                    public boolean remove(Movie object) {
                        boolean remove = movieList.remove(object);
                        updateDatabase();
                        return remove;
                    }

                    @Override
                    public Movie createOrUpdate(Movie object) {
                        if (object == null) {
                            return null;
                        }

                        if (object.isNewObject()) {
                            object.setId(generateNextId());
                            movieList.add(object);
                            updateDatabase();
                            return object;
                        }

                        Movie movie = findById(object.getId());

                        movie.setName(object.getName());
                        movie.setStockCount(object.getStockCount());
                        movie.setType(object.getType());

                        updateDatabase();
                        return movie;
                    }

                    @Override
                    public int generateNextId() {
                        int i;
                        for (i = 1; i < Integer.MAX_VALUE; i++) {
                            try {
                                findById(i);
                            } catch (Exception e) {
                                break;
                            }
                        }
                        return i;
                    }

                    public void updateDatabase() {
                        try {
                            Map<String, Object> output = new HashMap<>();
                            output.put("movie", movieList.toArray());
                            output.put("customer", getCustomerTable().getAll().toArray());
                            output.put("order", getOrderTable().getAll().toArray());

                            String content = "";
                            if (filePath.endsWith(".yaml")) {
                                yamlMapper.writerWithDefaultPrettyPrinter().writeValue(file, output);
                                String yaml = FileUtils.readFileToString(file, "utf-8");
                                Object obj = yamlMapper.readValue(yaml, Object.class);
                                content = jsonMapper.writeValueAsString(obj);

                            } else {
                                jsonMapper.writerWithDefaultPrettyPrinter().writeValue(file, output);
                                content = FileUtils.readFileToString(file, "utf-8");
                            }
                            finalJsonNode[0] = jsonMapper.readTree(content);

                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                        }
                    }

                };
            }

            @Override
            public DBTableRepository<Customer> getCustomerTable() {

                final List<Customer> customerList = new ArrayList<>();

                String customers = finalJsonNode[0].get("customer").toString();

                try {
                    Customer[] all = jsonMapper.readValue(customers, Customer[].class);
                    customerList.addAll(Arrays.asList(all));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return new DBTableRepository<Customer>() {
                    @Override
                    public List<Customer> getAll() {
                        return customerList;
                    }

                    @Override
                    public Customer findById(int id) {
                        return getAll().stream().filter(customer -> customer.getId() == id).findFirst().get();
                    }

                    @Override
                    public boolean remove(Customer object) {
                        boolean remove = customerList.remove(object);
                        updateDatabase();
                        return remove;
                    }

                    @Override
                    public Customer createOrUpdate(Customer object) {
                        if (object == null) {
                            return null;
                        }

                        if (object.isNewObject()) {
                            object.setId(generateNextId());
                            customerList.add(object);
                            updateDatabase();
                            return object;
                        }

                        Customer customer = findById(object.getId());

                        customer.setName(object.getName());
                        customer.setPoints(object.getPoints());

                        updateDatabase();
                        return customer;
                    }

                    @Override
                    public int generateNextId() {
                        int i;
                        for (i = 1; i < Integer.MAX_VALUE; i++) {
                            try {
                                findById(i);
                            } catch (Exception e) {
                                break;
                            }
                        }
                        return i;
                    }

                    public void updateDatabase() {
                        try {
                            Map<String, Object> output = new HashMap<>();
                            output.put("movie", getMovieTable().getAll().toArray());
                            output.put("customer", customerList.toArray());
                            output.put("order", getOrderTable().getAll().toArray());

                            String content = "";
                            if (filePath.endsWith(".yaml")) {
                                yamlMapper.writerWithDefaultPrettyPrinter().writeValue(file, output);
                                String yaml = FileUtils.readFileToString(file, "utf-8");
                                Object obj = yamlMapper.readValue(yaml, Object.class);
                                content = jsonMapper.writeValueAsString(obj);

                            } else {
                                jsonMapper.writerWithDefaultPrettyPrinter().writeValue(file, output);
                                content = FileUtils.readFileToString(file, "utf-8");
                            }
                            finalJsonNode[0] = jsonMapper.readTree(content);

                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                        }
                    }
                };
            }

            @Override
            public DBTableRepository<RentOrder> getOrderTable() {

                final List<RentOrder> orderList = new ArrayList<>();

                Iterator<JsonNode> iterator = finalJsonNode[0].get("order").elements();

                while (iterator.hasNext()) {
                    JsonNode i = iterator.next();
                    RentOrder rentOrder = new RentOrder();
                    rentOrder.setId(i.get("id").asInt());
                    rentOrder.setCustomer(getCustomerTable().findById(i.get("customer").asInt()));
                    rentOrder.setOrderDate(LocalDate.parse(i.get("orderDate").asText()));
                    List<RentOrder.Item> itemsList = new ArrayList<>();
                    try {
                        RentOrder.Item[] items = jsonMapper.readValue(i.get("items").toString(), RentOrder.Item[].class);
                        for (int j = 0; j < items.length; j++) {
                            items[j].setMovie(getMovieTable().findById(i.get("items").get(j).get("movie").asInt()));
                        }
                        itemsList.addAll(Arrays.asList(items));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    rentOrder.setItems(itemsList);
                    orderList.add(rentOrder);
                }

                return new DBTableRepository<RentOrder>() {
                    @Override
                    public List<RentOrder> getAll() {
                        return orderList;
                    }

                    @Override
                    public RentOrder findById(int id) {
                        return getAll().stream().filter(order -> order.getId() == id).findFirst().get();
                    }

                    @Override
                    public boolean remove(RentOrder object) {
                        boolean remove = orderList.remove(object);
                        updateDatabase();
                        return remove;
                    }

                    @Override
                    public RentOrder createOrUpdate(RentOrder object) {
                        if (object == null) {
                            return null;
                        }

                        if (object.isNewObject()) {
                            object.setId(generateNextId());
                            orderList.add(object);
                            updateDatabase();
                            return object;
                        }

                        RentOrder order = findById(object.getId());

                        order.setCustomer(object.getCustomer());
                        order.setOrderDate(order.getOrderDate());
                        order.setItems(object.getItems());

                        updateDatabase();

                        return order;
                    }

                    @Override
                    public int generateNextId() {
                        int i;
                        for (i = 1; i < Integer.MAX_VALUE; i++) {
                            try {
                                findById(i);
                            } catch (Exception e) {
                                break;
                            }
                        }
                        return i;
                    }

                    public void updateDatabase() {
                        try {
                            Map<String, Object> output = new HashMap<>();
                            output.put("movie", getMovieTable().getAll().toArray());
                            output.put("customer", getCustomerTable().getAll().toArray());
                            output.put("order", orderList.toArray());

                            String content = "";
                            if (filePath.endsWith(".yaml")) {
                                yamlMapper.writerWithDefaultPrettyPrinter().writeValue(file, output);
                                String yaml = FileUtils.readFileToString(file, "utf-8");
                                Object obj = yamlMapper.readValue(yaml, Object.class);
                                content = jsonMapper.writeValueAsString(obj);

                            } else {
                                jsonMapper.writerWithDefaultPrettyPrinter().writeValue(file, output);
                                content = FileUtils.readFileToString(file, "utf-8");
                            }
                            finalJsonNode[0] = jsonMapper.readTree(content);

                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                        }
                    }
                };
            }
        };
    }
}
