package tests;

import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import models.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static specs.ReqresSpec.*;

public class ReqresApiTests extends TestBase {
    @Feature("Тестирование запроса на успешное создание пользователя")
    @DisplayName("Создание пользователя")
    @Tag("account")
    @Test
    @Severity(SeverityLevel.BLOCKER)
    public void createUserTest() {
        CreateUserBodyModel userData = new CreateUserBodyModel();
        userData.setName(data.name);
        userData.setJob(data.job);
        CreateUserResponseModel response = step("Отправляем запрос", () ->
                given()
                        .spec(requestSpec)
                        .body(userData)
                        .when()
                        .post("/users")
                        .then()
                        .spec(createdResponseSpec)
                        .extract().as(CreateUserResponseModel.class));
        step("Проверяем, что имя соответствует заданному", () ->
                assertThat(response.getName(), equalTo(userData.getName())));
        step("Проверяем, что работа соответствует заданной", () ->
                assertThat(response.getJob(), equalTo(userData.getJob())));
        step("Проверяем, что id не пустой", () ->
                assertThat(response.getId(), notNullValue()));
        given(requestSpec).delete("users/" + response.getId());
    }

    @Feature("Тестирование запроса на успешное редактирование пользователя")
    @DisplayName("Редактирование пользователя")
    @Tag("account")
    @Test
    @Severity(SeverityLevel.BLOCKER)
    public void updateUserTest() {
        CreateUserBodyModel userData = new CreateUserBodyModel();
        CreateUserBodyModel userDataUpdate = new CreateUserBodyModel();
        userData.setName(data.name);
        userData.setJob(data.job);
        CreateUserResponseModel response = step("Отправляем запрос на создание пользователя", () ->
                given()
                        .spec(requestSpec)
                        .body(userData)
                        .when()
                        .post("/users")
                        .then()
                        .spec(createdResponseSpec)
                        .extract().as(CreateUserResponseModel.class));
        userData.setName(data.nameUpdate);
        userData.setJob(data.jobUpdate);
        CreateUserResponseModel responseUpdate = step("Отправляем запрос на редактирование созданного пользователя", () ->
                given()
                        .spec(requestSpec)
                        .body(userData)
                        .when()
                        .put("users/" + response.getId())
                        .then()
                        .spec(successful200ResponseSpec)
                        .extract().as(CreateUserResponseModel.class));
        step("Проверяем, что имя соответствует новому отредактированному", () ->
                assertThat(responseUpdate.getName(), equalTo(userDataUpdate.getName())));
        step("Проверяем, что работа соответствует новой отредактированной", () ->
                assertThat(responseUpdate.getJob(), equalTo(userDataUpdate.getJob())));
        given(requestSpec).delete("users/" + response.getId());

    }

    @Feature("Тестирование запроса на успешное удаление пользователя")
    @DisplayName("Удаление пользователя")
    @Tag("account")
    @Test
    @Severity(SeverityLevel.BLOCKER)
    public void deleteUserTest() {
        CreateUserBodyModel userData = new CreateUserBodyModel();
        userData.setName(data.name);
        userData.setJob(data.job);
        CreateUserResponseModel response = step("Отправляем запрос на создание пользователя", () ->
                given()
                        .spec(requestSpec)
                        .body(userData)
                        .when()
                        .post("/users")
                        .then()
                        .spec(createdResponseSpec)
                        .extract().as(CreateUserResponseModel.class));
        String deletedResult = step("Отправляем запрос на удаление пользователя", () ->
                given(requestSpec)
                        .when()
                        .delete("users/" + response.getId())
                        .then()
                        .spec(successful204ResponseSpec)
                        .extract().asString());
        step("Проверяем, что возвращается пустой результат", () ->
                assertThat(deletedResult, equalTo("")));
    }

    @Feature("Тестирование запроса на неуспешную регистрацию пользователя")
    @DisplayName("Проверяем ошибку при регистрации пользователя, который не входит в список разрешенных")
    @Tag("account")
    @Test
    @Severity(SeverityLevel.CRITICAL)
    void unsuccessfulRegistrationTest() {
        LoginBodyModel userData = new LoginBodyModel();
        userData.setEmail(data.email);
        userData.setPassword(data.password);
        ErrorRequestModel response = step("Отправляем запрос на регистрацию с некорректными email и паролем ", () ->
                given(requestSpec)
                        .body(userData)
                        .when()
                        .post("/register")
                        .then()
                        .spec(error400RequestResponseSpec)
                        .extract().as(ErrorRequestModel.class));
        step("Проверяем полученную ошибку", () ->
                assertThat(response.getError(), equalTo("Note: Only defined users succeed registration")));
    }

    @Feature("Тестирование запроса на успешное получение данных пользователя")
    @DisplayName("Проверяем данные пользователя, запрошенные по id")
    @Tag("userData")
    @Test
    @Severity(SeverityLevel.CRITICAL)
    void fetchUserInfoTest() {
        GetUserResponseModel response = step("Отправляем запрос на получение данных", () ->
                given(requestSpec)
                        .when()
                        .get("users/{id}", 1)
                        .then()
                        .spec(successful200ResponseSpec)
                        .extract().as(GetUserResponseModel.class));
        step("Проверяем, что id = 1", () ->
                assertThat(response.getData().getId(), equalTo(1)));
        step("Проверяем, что first_name = George", () ->
                assertThat(response.getData().getFirst_name(), equalTo("George")));
        step("Проверяем, что email = george.bluth@reqres.in", () ->
                assertThat(response.getData().getEmail(), equalTo("george.bluth@reqres.in")));
    }

    @Feature("Тестирование запроса на неуспешное получение данных пользователя")
    @DisplayName("Неуспешное получение пользователя по id")
    @Tag("userData")
    @Test
    @Severity(SeverityLevel.NORMAL)
    void getNonexistentUserTest() {
        step("Отправляем запрос на получение данных пользователя по некорреткному id", () ->
                given(requestSpec)
                        .when()
                        .get("users/{id}", 23)
                        .then()
                        .spec(error404ResponseSpec)
                        .extract().as(CreateUserResponseModel.class));
    }

    @Feature("Тестирование запроса на наличие названия цвета в базе данных")
    @DisplayName("Проверем наличие выбранного наименования цвета")
    @Tag("userData")
    @Test
    @Severity(SeverityLevel.NORMAL)
    void singleColourTest() {
        step("Отправляем запрос на получение информации о цвете", () ->
                given(requestSpec)
                        .when()
                        .get("/unknown")
                        .then()
                        .spec(successful200ResponseSpec)
                        .body("data.findAll{it.name =~/./}.name.flatten()",
                                hasItem("tigerlily")));
    }
}
