package com.api.tests;

import com.api.config.Config;
import com.api.models.Book;
import com.api.tests.base.BaseTest;
import com.api.utils.AllureUtil;
import com.api.utils.ApiUtils;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.*;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

@Slf4j
@Epic("Books API Testing")
@Feature("CRUD Operations for Books")
public class BooksApiTest extends BaseTest {

    private static final int BOOK_CREATION_TIMEOUT_MS = 7000;
    private static final int BOOK_DELETION_TIMEOUT_MS = 6000;
    private static final int NON_EXISTENT_ID = 999999;

    @BeforeClass
    @Step("Setup test environment")
    public void setUp() {
        RestAssured.baseURI = Config.getBaseUrl();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        log.info("Base URL set to: {}", RestAssured.baseURI);
    }

    @AfterClass
    @Step("Cleanup test environment")
    public void tearDown() {
        RestAssured.reset();
        log.info("RestAssured reset");
    }

    // ---------- Read ----------
    @Test(priority = 1, enabled = false)
    @TmsLink("CSP-0001")
    @Story("Read Operations")
    @Description("Retrieve all books")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetAllBooks() {
        Response response = ApiUtils.getAllBooks();
        attachResponse("Get All Books", response);
        response.then().statusCode(200);

        List<Book> books = response.jsonPath().getList("", Book.class);
        assertNotNull(books, "Books list should not be null");
        if (!books.isEmpty()) {
            validateBook(books.get(0));
        }
    }

    @Test(priority = 2, enabled = false)
    @TmsLink("CSP-0002")
    @Story("Read Operations")
    @Description("Retrieve a specific book by ID")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetBookById() {
        Response allBooksResponse = ApiUtils.getAllBooks();
        attachResponse("All Books Response", allBooksResponse);
        assertEquals(allBooksResponse.getStatusCode(), 200, "Failed to get books: unexpected status code");

        List<Book> books = allBooksResponse.jsonPath().getList("", Book.class);
        assertFalse(books.isEmpty(), "No books returned by API");

        Book firstBook = books.get(0);
        Response getByIdResponse = ApiUtils.getBookById(firstBook.getId());
        attachResponse("Get Book By ID", getByIdResponse);
        assertEquals(getByIdResponse.getStatusCode(), 200, "Failed to get book by ID");

        Book book = ApiUtils.parseBookFromResponse(getByIdResponse);
        assertEquals(book.getId(), firstBook.getId(), "Book ID mismatch");
        validateBook(book);
    }

    // ---------- Create ----------
    @Test(priority = 3, timeOut = BOOK_CREATION_TIMEOUT_MS)
    @TmsLink("CSP-0003")
    @Story("Create Operations")
    @Description("Create a new book")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateBook() {
        Book newBook = new Book(
                "Refactoring: " + UUID.randomUUID(),
                "Martin Fowler",
                "Addison-Wesley Professional",
                "Programming",
                448,
                35.50
        );

        Response response = ApiUtils.createBook(newBook);
        attachResponse("Create Book", response);
        response.then().statusCode(anyOf(equalTo(200), equalTo(201)));

        Book createdBook = response.as(Book.class);
        assertTrue(createdBook.getId() > 0, "Created book should have a valid positive ID");
        assertBooksEqualIgnoringId(newBook, createdBook);
    }

    // ---------- Update ----------
    @Test(priority = 4, enabled = false)
    @TmsLink("CSP-0004")
    @Story("Update Operations")
    @Description("Update an existing book (self-sufficient)")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateBook() {
        List<Book> books = ApiUtils.getBooksList();
        Book bookToUpdate = books.isEmpty() ? createTempBook() : books.get(0);

        Book updatedBook = new Book(
                bookToUpdate.getId(),
                bookToUpdate.getName() + " - Updated",
                bookToUpdate.getAuthor(),
                bookToUpdate.getPublication(),
                bookToUpdate.getCategory(),
                bookToUpdate.getPages() + 10,
                bookToUpdate.getPrice() + 5.0
        );

        Response response = ApiUtils.updateBook(updatedBook.getId(), updatedBook);
        attachResponse("Update Book", response);
        response.then().statusCode(200);

        Book responseBook = response.as(Book.class);
        assertBooksEqual(updatedBook, responseBook);
    }

    // ---------- Delete ----------
    @Test(priority = 5, timeOut = BOOK_DELETION_TIMEOUT_MS)
    @TmsLink("CSP-0005")
    @Story("Delete Operations")
    @Description("Delete a book (self-sufficient)")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteBook() {
        Book bookToDelete = createTempBook();

        Response deleteResponse = ApiUtils.deleteBook(bookToDelete.getId());
        attachResponse("Delete Book", deleteResponse);
        deleteResponse.then().statusCode(anyOf(equalTo(200), equalTo(204)));

        Response getResponse = ApiUtils.getBookById(bookToDelete.getId());
        attachResponse("Get Deleted Book", getResponse);
        getResponse.then().statusCode(404);
    }

    // ---------- Negative Cases ----------
    @Test(priority = 6, enabled = false)
    @TmsLink("CSP-0006")
    @Story("Error Handling")
    @Description("Get non-existent book")
    @Severity(SeverityLevel.MINOR)
    public void testGetNonExistentBook() {
        Response response = ApiUtils.getBookById(NON_EXISTENT_ID);
        attachResponse("Get Non-existent Book", response);
        response.then().statusCode(404);
    }

    @Test(priority = 7)
    @TmsLink("CSP-0007")
    @Story("Error Handling")
    @Description("Update non-existent book")
    @Severity(SeverityLevel.MINOR)
    public void testUpdateNonExistentBook() {
        Book updatedBook = new Book(
                NON_EXISTENT_ID,
                "Non-existent Book",
                "Unknown",
                "Unknown",
                "Testing",
                100,
                10.0
        );

        Response response = ApiUtils.updateBook(NON_EXISTENT_ID, updatedBook);
        attachResponse("Update Non-existent Book", response);
        response.then().statusCode(404);
    }

    @Test(priority = 8)
    @TmsLink("CSP-0008")
    @Story("Error Handling")
    @Description("Delete non-existent book")
    @Severity(SeverityLevel.MINOR)
    public void testDeleteNonExistentBook() {
        Response response = ApiUtils.deleteBook(NON_EXISTENT_ID);
        attachResponse("Delete Non-existent Book", response);
        response.then().statusCode(404);
    }

    @Test(priority = 9, enabled = false)
    @TmsLink("CSP-0009")
    @Story("Error Handling")
    @Description("Create book with invalid data")
    @Severity(SeverityLevel.MINOR)
    public void testCreateBookWithInvalidData() {
        Book invalidBook = new Book("", "", "", "", -1, -10.50);
        Response response = ApiUtils.createBook(invalidBook);
        attachResponse("Create Invalid Book", response);
        response.then().statusCode(anyOf(equalTo(400), equalTo(422)));
    }

    // ---------- Data Driven ----------
    @DataProvider(name = "bookData")
    public Object[][] bookDataProvider() {
        return new Object[][]{
                {"Clean Architecture", "Robert C. Martin", "Prentice Hall", "Programming", 432, 28.99},
                {"Design Patterns", "Gang of Four", "Addison-Wesley", "Programming", 395, 45.00},
                {"The Pragmatic Programmer", "David Thomas", "Addison-Wesley", "Programming", 352, 42.95}
        };
    }

    @Test(dataProvider = "bookData", priority = 10, enabled = false)
    @TmsLink("CSP-0010")
    @Story("Data-Driven Testing")
    @Description("Create multiple books with different data sets")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateMultipleBooks(String name, String author, String publication,
                                        String category, int pages, double price) {
        Book newBook = new Book(name, author, publication, category, pages, price);
        Response response = ApiUtils.createBook(newBook);
        attachResponse("Create Book (DDT)", response);
        response.then().statusCode(anyOf(equalTo(200), equalTo(201)));

        Book createdBook = response.as(Book.class);
        assertTrue(createdBook.getId() > 0, "Created book should have a valid positive ID");
        assertBooksEqualIgnoringId(newBook, createdBook);

        // Cleanup
        ApiUtils.deleteBook(createdBook.getId());
    }

    // ---------- Utilities ----------
    private void validateBook(Book book) {
        assertTrue(book.getId() > 0, "Book ID must be positive");
        assertTrue(book.getName() != null && !book.getName().isBlank(), "Book name should not be blank");
        assertTrue(book.getAuthor() != null && !book.getAuthor().isBlank(), "Book author should not be blank");
        assertTrue(book.getPublication() != null && !book.getPublication().isBlank(), "Book publication should not be blank");
        assertTrue(book.getCategory() != null && !book.getCategory().isBlank(), "Book category should not be blank");
        assertTrue(book.getPages() > 0, "Pages must be positive");
        assertTrue(book.getPrice() > 0, "Price must be positive");
    }

    private void assertBooksEqual(Book expected, Book actual) {
        assertEquals(actual.getId(), expected.getId(), "Book ID mismatch");
        assertEquals(actual.getName(), expected.getName(), "Book name mismatch");
        assertEquals(actual.getAuthor(), expected.getAuthor(), "Book author mismatch");
        assertEquals(actual.getPublication(), expected.getPublication(), "Book publication mismatch");
        assertEquals(actual.getCategory(), expected.getCategory(), "Book category mismatch");
        assertEquals(actual.getPages(), expected.getPages(), "Book pages mismatch");
        assertEquals(actual.getPrice(), expected.getPrice(), "Book price mismatch");
    }

    private void assertBooksEqualIgnoringId(Book expected, Book actual) {
        assertEquals(actual.getName(), expected.getName(), "Book name mismatch");
        assertEquals(actual.getAuthor(), expected.getAuthor(), "Book author mismatch");
        assertEquals(actual.getPublication(), expected.getPublication(), "Book publication mismatch");
        assertEquals(actual.getCategory(), expected.getCategory(), "Book category mismatch");
        assertEquals(actual.getPages(), expected.getPages(), "Book pages mismatch");
        assertEquals(actual.getPrice(), expected.getPrice(), "Book price mismatch");
    }

    @Step("Attach response: {name}")
    private void attachResponse(String name, Response response) {
        try {
            AllureUtil.attachJson(name, response.asPrettyString());
        } catch (Exception e) {
            log.warn("Failed to attach response '{}': {}", name, e.getMessage());
        }
    }

    @Step("Create temporary book for test precondition")
    private Book createTempBook() {
        Book temp = ApiUtils.generateRandomBook();
        Response response = ApiUtils.createBook(temp);
        attachResponse("Create Temp Book", response);
        return response.as(Book.class);
    }
}