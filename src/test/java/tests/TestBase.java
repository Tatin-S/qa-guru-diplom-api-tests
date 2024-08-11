package tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import data.TestData;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class TestBase {
    TestData data;
    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://reqres.in";
        RestAssured.basePath = "/api";
    }

    @BeforeEach
    void testDataGeneration()
    {
        data = new TestData();
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
    }
}