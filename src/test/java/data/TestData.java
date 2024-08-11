package data;

import com.github.javafaker.Faker;
import com.github.javafaker.IdNumber;
import com.github.javafaker.service.RandomService;

public class TestData {
    Faker faker = new Faker();
    public String nameUpdate = faker.name().firstName(),
            name = faker.name().firstName(),
            job = faker.job().position(),
            jobUpdate = faker.job().position(),
            email = faker.internet().emailAddress(),
            password = String.valueOf(faker.random());
}
