package database;

import org.junit.Assert;
import org.junit.Test;
import test.fujitsu.videostore.backend.database.Database;
import test.fujitsu.videostore.backend.database.DatabaseFactory;

public class FactoryTest {



    @Test
    public void testAddCustomer() {
        Database db = DatabaseFactory.from("database.json");

    }
}
