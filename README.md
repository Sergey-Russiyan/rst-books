# Books API Test Suite

This project contains comprehensive automated tests for the Books API using TestNG, Rest Assured, and Allure reporting framework.

## Test Coverage

The test suite covers all CRUD operations for the Books API:

### Implemented Test Cases

1. **Read Operations**
    - Get all books
    - Get specific book by ID

2. **Create Operations**
    - Create a new book with valid data
    - Create multiple books (data-driven testing)

3. **Update Operations**
    - Update existing book

4. **Delete Operations**
    - Delete a book
    - Verify deletion

5. **Error Handling**
    - Get non-existent book (404)
    - Create book with invalid data (400/422)

6. **Authentication & Security**
    - Unauthorized access (401)
    - Invalid credentials (401)

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Valid API credentials (username/password)

## Project Structure

```
src/
├── main/java/
│   └── com/api/models/
│       └── Book.java                 # Book model/POJO class
└── test/
    ├── java/com/api/tests/
    │   └── BooksApiTest.java         # Main test class
    └── resources/
        ├── testng.xml                # TestNG suite configuration
        ├── allure.properties         # Allure configuration
        └── categories.json           # Allure test categories
```

## Setup and Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd books-api-tests
   ```

2. **Install dependencies**
   ```bash
   mvn clean install
   ```

3. **Configure API credentials**
   Set environment variables or system properties:
   ```bash
   export API_USERNAME=your_username
   export API_PASSWORD=your_password
   ```

   Or pass as Maven properties:
   ```bash
   mvn test -Dapi.username=your_username -Dapi.password=your_password
   ```

## Running Tests

### Run all tests
```bash
mvn clean test
```

### Run with specific profile
```bash
mvn clean test -Papi-tests -Dapi.username=testuser -Dapi.password=testpass
```

### Run specific test groups
```bash
# Run only CRUD operations
mvn test -Dgroups="crud"

# Run only error handling tests
mvn test -Dgroups="error-handling"
```

## Allure Reporting

### Generate Allure Report
```bash
# Generate and serve report
mvn allure:serve

# Generate report to target/site/allure-maven-plugin
mvn allure:report
```

### View Reports
After running tests, Allure results are stored in `target/allure-results/`.

To generate and view the HTML report:
1. Install Allure CLI: `npm install -g allure-commandline`
2. Generate report: `allure generate target/allure-results --clean -o allure-report`
3. Open report: `allure open allure-report`

## Test Configuration

### API Configuration
- **Base URL**: `http://77.102.250.113:17354/api/v1`
- **Authentication**: Basic Auth (username/password)
- **Content-Type**: `application/json`

### TestNG Configuration
The `testng.xml` file defines test execution order and grouping:
- CRUD Operations (priority 1-5)
- Error Handling (priority 6-7)
- Authentication Tests (priority 8-9)
- Data-driven Tests (priority 10)

## Key Features

### Test Framework Features
- **TestNG**: Test execution, parallel execution, data providers
- **Rest Assured**: HTTP client for API testing
- **Allure**: Rich HTML reporting with screenshots and logs
- **Data-driven Testing**: Multiple test data sets using TestNG DataProvider
- **Error Handling**: Comprehensive negative test scenarios
- **Authentication Testing**: Security validation

### Reporting Features
- Test execution timeline
- Request/Response logging
- Test categorization
- Failure analysis
- Environment information
- Test trends and history

### Test Reliability
- Proper test dependencies and execution order
- Cleanup operations (delete created test data)
- Retry mechanisms for flaky tests
- Comprehensive assertions and validations

## API Endpoints Tested

| Method | Endpoint | Description | Test Coverage |
|--------|----------|-------------|---------------|
| GET | `/books` | Get all books | v             |
| GET | `/books/{id}` | Get book by ID | v             |
| POST | `/books` | Create new book | v             |
| PUT | `/books/{id}` | Update book | v             |
| DELETE | `/books/{id}` | Delete book | v             |

## Test Data

### Sample Book Object
```json
{
  "id": 1,
  "name": "Clean Code: A Handbook of Agile Software Craftsmanship",
  "author": "Robert C. Martin",
  "publication": "Prentice Hall",
  "category": "Programming",
  "pages": 464,
  "price": 22.0
}
```

### Data Provider Test Cases
The test suite includes data-driven tests with multiple book records:
- Clean Architecture by Robert C. Martin
- Design Patterns by Gang of Four
- The Pragmatic Programmer by David Thomas

## Troubleshooting

### Common Issues

1. **Authentication Errors (401)**
    - Verify username/password credentials
    - Check environment variables or system properties

2. **Connection Issues**
    - Verify API endpoint is accessible
    - Check network connectivity
    - Validate base URL configuration

3. **Test Dependencies**
    - Ensure tests run in correct order (priorities set)
    - Check for proper cleanup of test data

4. **Allure Report Issues**
    - Verify AspectJ weaver is properly configured
    - Check allure-results directory permissions
    - Ensure Allure CLI is installed for report generation

### Debug Mode
Run tests with verbose logging:
```bash
mvn test -Drest-assured.log=all -Dtestng.verbose=2
```

## Contributing

1. Follow existing code structure and naming conventions
2. Add appropriate Allure annotations (@Step, @Description, etc.)
3. Include both positive and negative test scenarios
4. Update documentation for new test cases
5. Ensure proper cleanup of test data

## Dependencies

- TestNG 7.8.0
- Rest Assured 5.3.2
- Allure TestNG 2.24.0
- Jackson 2.15.2
- Hamcrest 2.2

## License

This project is licensed under the MIT License.