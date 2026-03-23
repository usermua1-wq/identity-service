# Unit Test Configuration Guide for Identity Service

This document provides a comprehensive guide to the unit test configuration for the Identity Service project.

## Project Structure

The test suite is organized into the following layers:

```
src/test/java/com/devteria/identity_service/
├── configuration/
│   └── TestConfig.java              # Test bean configurations
├── controller/
│   └── UserControllerTest.java      # REST endpoint tests
├── repository/
│   └── UserRepositoryTest.java      # JPA repository tests
├── service/
│   └── UserServiceTest.java         # Service layer logic tests
├── util/
│   └── TestDataBuilder.java         # Test data factory
└── IdentityServiceApplicationTests.java  # Integration tests

src/test/resources/
└── application.yaml                  # Test configuration profile
```

## Dependencies

The following testing dependencies are configured in `pom.xml`:

### Core Testing
- **JUnit 5 (Jupiter)**: Modern testing framework with annotations like `@Test`, `@DisplayName`, `@BeforeEach`
- **Spring Boot Test**: Integration with Spring context for testing
- **MockMvc**: Testing REST endpoints without starting a server
- **Mockito**: Mocking framework for unit tests
- **AssertJ**: Fluent assertion library with better readability

### Database Testing
- **TestContainers**: Docker-based database containers for integration tests
- **H2 Database**: In-memory database for lightweight testing
- **Spring Data JPA Test**: `@DataJpaTest` for repository testing

## Test Layers

### 1. Unit Tests - Service Layer (`UserServiceTest.java`)

Tests the business logic of services using mocks for dependencies.

**Key Features:**
- Uses `@ExtendWith(MockitoExtension.class)` for lightweight unit testing
- Mocks repository, mapper, and password encoder
- Tests success and failure scenarios
- Demonstrates mock verification with `verify()`

**Running Service Tests:**
```bash
mvn test -Dtest=UserServiceTest
```

**Example Test:**
```java
@Test
@DisplayName("Should create user successfully")
void testCreateUserSuccess() {
    // Arrange
    when(userRepository.existsByUsername("newuser")).thenReturn(false);
    when(userMapper.toUser(userCreationRequest)).thenReturn(testUser);
    
    // Act
    User result = userService.createRequest(userCreationRequest);
    
    // Assert
    assertThat(result).isNotNull();
    verify(userRepository).existsByUsername("newuser");
}
```

### 2. Integration Tests - Controller Layer (`UserControllerTest.java`)

Tests REST endpoints with a running Spring context using MockMvc.

**Key Features:**
- Uses `@SpringBootTest` and `@AutoConfigureMockMvc`
- Mocks only the service layer
- Tests HTTP requests and responses
- Validates JSON response structure

**Running Controller Tests:**
```bash
mvn test -Dtest=UserControllerTest
```

**Example Test:**
```java
@Test
@DisplayName("POST /users - Create user successfully")
void testCreateUserEndpoint() throws Exception {
    // Arrange
    when(userService.createRequest(any(UserCreationRequest.class))).thenReturn(testUser);
    
    // Act & Assert
    mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userCreationRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.id").value("user-123"));
}
```

### 3. Repository Tests (`UserRepositoryTest.java`)

Tests JPA repository queries and database operations.

**Key Features:**
- Uses `@DataJpaTest` for lightweight database testing
- Tests CRUD operations and custom query methods
- Automatically uses H2 in-memory database for tests
- No service or controller layer involved

**Running Repository Tests:**
```bash
mvn test -Dtest=UserRepositoryTest
```

**Example Test:**
```java
@Test
@DisplayName("Should find user by username")
void testFindUserByUsername() {
    // Arrange
    userRepository.save(testUser);
    
    // Act
    Optional<User> foundUser = userRepository.findByUsername("testuser");
    
    // Assert
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
}
```

### 4. Integration Tests - Application Context (`IdentityServiceApplicationTests.java`)

Verifies that the entire application context loads successfully.

**Running Application Tests:**
```bash
mvn test -Dtest=IdentityServiceApplicationTests
```

## Test Configuration

### TestConfig.java
Provides Spring beans used during testing, such as `PasswordEncoder`.

### application.yaml (Test Profile)
Configures test-specific settings:
- Uses H2 in-memory database
- Sets `hibernate.ddl-auto` to `create-drop` to auto-create and drop schema
- Disables SQL logging for cleaner output
- Increases logging levels for debugging

## Running Tests

### Run All Tests
```bash
mvn clean test
```

### Run Specific Test Class
```bash
mvn test -Dtest=UserServiceTest
```

### Run Specific Test Method
```bash
mvn test -Dtest=UserServiceTest#testCreateUserSuccess
```

### Run Tests with Coverage
```bash
mvn clean test jacoco:report
```

### Run Tests in Watch Mode (Continuous)
```bash
mvn clean test -Dtest=UserServiceTest -f pom.xml --watch
```

## Test Naming Conventions

- **Class Names**: `*Test` or `*Tests` suffix
- **Method Names**: `test*` prefix or `@DisplayName` annotation
- **Display Names**: Use `@DisplayName("Human readable description")`

Examples:
```java
@Test
@DisplayName("Should create user successfully")
void testCreateUserSuccess() { }

@Test
@DisplayName("POST /users - Create user successfully")
void testCreateUserEndpoint() { }
```

## Best Practices

### 1. Test Isolation
- Use `@BeforeEach` to initialize test data before each test
- Avoid sharing test data between test methods
- Use mocks to isolate units under test

### 2. Assertions
- Use AssertJ for fluent assertions
- Test both success and failure scenarios
- Verify method calls with Mockito `verify()`

```java
assertThat(result).isNotNull();
assertThat(result.getUsername()).isEqualTo("testuser");
verify(userRepository).findById("user-123");
```

### 3. Test Data
- Use `TestDataBuilder` for consistent test data
- Keep test data builders flexible and reusable
- Avoid hard-coded values in multiple tests

### 4. Mocking
- Mock external dependencies (repositories, services, external APIs)
- Use `@Mock` for field injection
- Use `@InjectMocks` to inject mocks into the class under test

### 5. Testing Different Layers
- **Unit Tests**: Test individual methods with mocks
- **Integration Tests**: Test API endpoints with Spring context
- **Repository Tests**: Test database operations
- **End-to-End Tests**: Test complete workflows

## Common Testing Patterns

### Arrange-Act-Assert (AAA)
```java
@Test
void testExample() {
    // Arrange - Set up test data and mocks
    when(repository.findById("1")).thenReturn(Optional.of(user));
    
    // Act - Execute the method being tested
    UserDTO result = service.getUser("1");
    
    // Assert - Verify the results
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("1");
}
```

### Exception Testing
```java
@Test
void testThrowsException() {
    when(repository.findById("invalid")).thenReturn(Optional.empty());
    
    assertThatThrownBy(() -> service.getUser("invalid"))
            .isInstanceOf(AppException.class);
}
```

### Argument Matching with Mockito
```java
// Exact value
when(repository.save(testUser)).thenReturn(testUser);

// Any argument
when(repository.save(any(User.class))).thenReturn(testUser);

// Multiple verifications
verify(repository).save(any(User.class));
verify(repository, times(2)).findById(any());
verify(repository, never()).delete(any());
```

## Troubleshooting

### Issue: "No bean found" error in tests
**Solution**: Ensure `@SpringBootTest` or appropriate test annotation is used, or create beans in `TestConfig.java`

### Issue: Tests fail due to database state
**Solution**: Ensure `@DataJpaTest` uses `create-drop` mode, or use `@Transactional` for rollback

### Issue: MockMvc returns 404
**Solution**: Verify controller path matches the test URL, and ensure `@AutoConfigureMockMvc` is present

### Issue: Security test failures
**Solution**: Use `@WithMockUser` or `@WithAnonymousUser` annotations for authenticated tests

```java
@Test
@WithMockUser(username = "admin", roles = "ADMIN")
void testSecuredEndpoint() { }
```

## Next Steps

1. **Add More Test Classes**: Create tests for other services and controllers
2. **Security Testing**: Add tests for authentication and authorization
3. **Performance Testing**: Add performance test cases for critical operations
4. **Integration Tests**: Add end-to-end tests with TestContainers for database
5. **Coverage Analysis**: Use JaCoCo plugin to measure test coverage
6. **CI/CD Integration**: Configure tests to run in your CI/CD pipeline

## Resources

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing Guide](https://spring.io/guides/gs/testing-web/)
- [AssertJ Documentation](https://assertj.org/docs/)
- [TestContainers Documentation](https://www.testcontainers.org/)

