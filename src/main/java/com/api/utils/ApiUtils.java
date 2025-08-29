package com.api.utils;

import com.api.config.Config;
import com.api.models.Book;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Allure;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.List;

import static io.restassured.RestAssured.given;

@Slf4j
@UtilityClass
public class ApiUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String USERNAME = Config.getUsername();
    private static final String PASSWORD = Config.getPassword();

    public static Response getAllBooks() {
        return Allure.step("Get all books from API", () -> {
            Response response = given()
                    .auth().preemptive().basic(USERNAME, PASSWORD)
                    .contentType("application/json")
                    .when()
                    .get(Config.getBooksEndpoint())
                    .then()
                    .extract().response();

            AllureUtil.attachJson("Get All Books Response", response.asPrettyString());
            return response;
        });
    }

    public static Response getBookById(int bookId) {
        return Allure.step("Get book by ID: " + bookId, () -> {
            Response response = given()
                    .auth().preemptive().basic(USERNAME, PASSWORD)
                    .contentType("application/json")
                    .pathParam("id", bookId)
                    .when()
                    .get(Config.getBooksEndpoint() + "/{id}")
                    .then()
                    .extract().response();

            AllureUtil.attachJson("Get Book By ID Response", response.asPrettyString());
            return response;
        });
    }

    public static Response createBook(Book book) {
        return Allure.step("Create new book test object: " + book.getName(), () -> {
            try {
                String bookJson = objectMapper.writeValueAsString(book);

                AllureUtil.attachJson("Create Book Request", bookJson);

                Response response = given()
                        .auth().preemptive().basic(USERNAME, PASSWORD)
                        .contentType("application/json")
                        .body(bookJson)
                        .when()
                        .post(Config.getBooksEndpoint())
                        .then()
                        .extract().response();

                AllureUtil.attachJson("Create Book Response", response.asPrettyString());
                return response;
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize book object", e);
            }
        });
    }

    public static Response updateBook(int bookId, Book book) {
        return Allure.step("Update book with ID: " + bookId, () -> {
            try {
                book.setId(bookId);
                String bookJson = objectMapper.writeValueAsString(book);

                AllureUtil.attachJson("Update Book Request", bookJson);

                Response response = given()
                        .contentType("application/json")
                        .pathParam("id", bookId)
                        .body(bookJson)
                        .when()
                        .put(Config.getBooksEndpoint() + "/{id}")
                        .then()
                        .extract().response();

                AllureUtil.attachJson("Update Book Response", response.asPrettyString());
                return response;
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize book object", e);
            }
        });
    }

    public static Response deleteBook(int bookId) {
        return Allure.step("Delete book with ID: " + bookId, () -> {
            Response response = given()
                    .contentType("application/json")
                    .pathParam("id", bookId)
                    .when()
                    .delete(Config.getBooksEndpoint() + "/{id}")
                    .then()
                    .extract().response();

            AllureUtil.attachJson("Delete Book Response", response.asPrettyString());
            return response;
        });
    }

    public static Integer getFirstAvailableBookId() {
        return Allure.step("Get first available book ID", () -> {
            Response response = getAllBooks();
            if (response.getStatusCode() == 200) {
                List<Book> books = parseBooksList(response);
                if (!books.isEmpty()) return books.get(0).getId();
            }
            return null;
        });
    }

    public static List<Book> getBooksList() {
        return Allure.step("Get books list", () -> {
            Response response = getAllBooks();
            if (response.getStatusCode() == 200) {
                return parseBooksList(response);
            }
            return List.of();
        });
    }

    // Helper method to handle array or single object
    private static List<Book> parseBooksList(Response response) {
        Object jsonObj = response.jsonPath().get();
        if (jsonObj instanceof List<?>) {
            return response.jsonPath().getList("", Book.class);
        } else if (jsonObj instanceof LinkedHashMap) {
            Book book = parseBookFromResponse(response);
            return List.of(book);
        } else {
            return List.of();
        }
    }

    public static Book parseBookFromResponse(Response response) {
        return Allure.step("Parse response to Book object", () -> {
            try {
                String json = response.asString();
                AllureUtil.attachJson("Parse Book Response", json);
                return objectMapper.readValue(json, Book.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to parse response to Book object", e);
            }
        });
    }

    public static Book generateRandomBook() {
        return Allure.step("Generate random book data", () -> {
            String[] names = {
                    "The Art of Software Testing",
                    "Effective Java",
                    "Spring in Action",
                    "Microservices Patterns",
                    "Building Microservices"
            };
            String[] authors = {
                    "Glenford Myers",
                    "Joshua Bloch",
                    "Craig Walls",
                    "Chris Richardson",
                    "Sam Newman"
            };
            String[] publications = {
                    "Wiley",
                    "Addison-Wesley",
                    "Manning Publications",
                    "O'Reilly Media",
                    "Packt Publishing"
            };
            int randomIndex = (int) (Math.random() * names.length);
            int pages = 200 + (int) (Math.random() * 500);
            double price = 20.0 + (Math.random() * 50.0);

            Book book = new Book(
                    names[randomIndex],
                    authors[randomIndex],
                    publications[randomIndex],
                    "Programming",
                    pages,
                    Math.round(price * 100.0) / 100.0
            );

            try {
                AllureUtil.attachJson("Generated Book", objectMapper.writeValueAsString(book));
            } catch (JsonProcessingException ignored) {
            }

            return book;
        });
    }
}