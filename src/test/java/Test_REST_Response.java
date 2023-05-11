import classes.Handler_Card;
import classes.Handler_User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import classes.User;
import restServer.ResponseGenerator;
import restServer.HttpRequest;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class Test_REST_Response {

    @Mock
    Handler_User handlerUser;
    @Mock
    Handler_Card handlerCard;
    @Mock
    User user;
    @Mock
    BufferedWriter writer;

    HttpRequest request;

    /**
     * Set up the test environment before each test.
     * This method initializes the HttpRequest object
     * and sets the headers and body for the request.
     */
    @BeforeEach
    void prepare() {
        // Initialize the HttpRequest object
        initializeHttpRequest();

        // Set the headers for the HTTP request
        setRequestHeaders();

        // Set the body for the HTTP request
        setRequestBody();
    }

    /**
     * Initialize the HttpRequest object.
     */
    private void initializeHttpRequest() {
        request = new HttpRequest();
        request.setVersion("HTTP/1.1");
    }

    /**
     * Set the headers for the HTTP request.
     */
    private void setRequestHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("user-agent:", "Mozilla/5.0");
        headers.put("content-length:", "10");
        headers.put("content-type:", "application/json");
        headers.put("accept:", "application/json");
        headers.put("host:", "localhost:8080");
        headers.put("authorization:", "test");

        request.setHeaders(headers);
    }

    /**
     * Set the body for the HTTP request.
     */
    private void setRequestBody() {
        request.setBody("{\"Username\":\"Test\", \"Password\":\"test\"}");
    }

/////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Test the user registration process.
     * This test verifies that the registerUser method is called
     * and the response is generated correctly.
     * @throws IOException if there's an issue with the writer
     */
    @Test
    public void testRegister() throws IOException {
        // Set up the test environment for user registration
        try (MockedStatic<Handler_User> mockedHandlerUser = Mockito.mockStatic(Handler_User.class)) {
            prepareTestRegister(mockedHandlerUser);

            // Perform the actual test
            executeTestRegister();
        }
    }

    /**
     * Set up the test environment for user registration.
     * @param mockedHandlerUser the mocked Handler_User object
     */
    private void prepareTestRegister(MockedStatic<Handler_User> mockedHandlerUser) {
        // Configure the mocked Handler_User instance
        mockedHandlerUser.when(Handler_User::getInstance)
                .thenReturn(handlerUser);

        // Set request method and resource
        request.setMethod("POST");
        request.setResource("/users");
    }

    /**
     * Execute the user registration test.
     * @throws IOException if there's an issue with the writer
     */
    private void executeTestRegister() throws IOException {
        // Create a ResponseGenerator object
        ResponseGenerator responseGenerator = new ResponseGenerator(writer);

        // Generate the response for the given request
        responseGenerator.generateResponse(request);

        // Verify if the registerUser method is called with the expected parameters
        verify(handlerUser).signUpUser(anyString(), anyString());

        // Verify if the flush method of the writer is called
        verify(writer).flush();
    }


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Test the user login process.
     * This test verifies that the loginUser method is called
     * and the response is generated correctly.
     * @throws IOException if there's an issue with the writer
     */
    @Test
    public void testSignin() throws IOException {
        // Set up the test environment for user login
        try (MockedStatic<Handler_User> mockedHandlerUser = Mockito.mockStatic(Handler_User.class)) {
            prepareTestSignin(mockedHandlerUser);

            // Perform the actual test
            executeTestSignin();
        }
    }

    /**
     * Set up the test environment for user login.
     * @param mockedHandlerUser the mocked Handler_User object
     */
    private void prepareTestSignin(MockedStatic<Handler_User> mockedHandlerUser) {
        // Configure the mocked Handler_User instance
        mockedHandlerUser.when(Handler_User::getInstance)
                .thenReturn(handlerUser);

        // Set request method and resource
        request.setMethod("POST");
        request.setResource("/sessions");
    }

    /**
     * Execute the user login test.
     * @throws IOException if there's an issue with the writer
     */
    private void executeTestSignin() throws IOException {
        // Create a ResponseGenerator object
        ResponseGenerator handler = new ResponseGenerator(writer);

        // Generate the response for the given request
        handler.generateResponse(request);

        // Verify if the loginUser method is called with the expected parameters
        verify(handlerUser).UserSignIn(anyString(), anyString());

        // Verify if the flush method of the writer is called
        verify(writer).flush();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Test the user logout process.
     * This test verifies that the logoutUser method is called
     * and the response is generated correctly.
     * @throws IOException if there's an issue with the writer
     */
    @Test
    public void testSignout() throws IOException {
        // Set up the test environment for user logout
        try (MockedStatic<Handler_User> mockedHandlerUser = Mockito.mockStatic(Handler_User.class)) {
            prepareTestSignout(mockedHandlerUser);

            // Perform the actual test
            executeTestSignout();
        }
    }

    /**
     * Set up the test environment for user logout.
     * @param mockedHandlerUser the mocked Handler_User object
     */
    private void prepareTestSignout(MockedStatic<Handler_User> mockedHandlerUser) {
        // Configure the mocked Handler_User instance
        mockedHandlerUser.when(Handler_User::getInstance)
                .thenReturn(handlerUser);

        // Set request method and resource
        request.setMethod("DELETE");
        request.setResource("/sessions");
    }

    /**
     * Execute the user logout test.
     * @throws IOException if there's an issue with the writer
     */
    private void executeTestSignout() throws IOException {
        // Create a ResponseGenerator object
        ResponseGenerator handler = new ResponseGenerator(writer);

        // Generate the response for the given request
        handler.generateResponse(request);

        // Verify if the logoutUser method is called with the expected parameters
        verify(handlerUser).SignOutUser(anyString(), anyString());

        // Verify if the flush method of the writer is called
        verify(writer).flush();
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Test the user edit process.
     * This test verifies that the setUserInfo method is called
     * and the response is generated correctly.
     * @throws IOException if there's an issue with the writer
     */
    @Test
    public void testChangeUser() throws IOException {
        // Set up the test environment for user edit
        try (MockedStatic<Handler_User> mockedHandlerUser = Mockito.mockStatic(Handler_User.class)) {
            prepareTestChangeUser(mockedHandlerUser);

            // Perform the actual test
            executeTestChangeUser();
        }
    }

    /**
     * Set up the test environment for user edit.
     * @param mockedHandlerUser the mocked Handler_User object
     */
    private void prepareTestChangeUser(MockedStatic<Handler_User> mockedHandlerUser) {
        // Configure the mocked Handler_User instance
        mockedHandlerUser.when(Handler_User::getInstance).thenReturn(handlerUser);

        // Set up the user authorization
        when(handlerUser.authorize(anyString())).thenReturn(user);
        when(user.getUsername()).thenReturn("kienboec");

        // Set request method, resource, and body
        request.setMethod("PUT");
        request.setResource("/users/kienboec");
        request.setBody("{\"Name\": \"Kienboeck\",  \"Bio\": \"me playin...\", \"Image\": \":-)\"}");
    }

    /**
     * Execute the user edit test.
     * @throws IOException if there's an issue with the writer
     */
    private void executeTestChangeUser() throws IOException {
        // Create a ResponseGenerator object
        ResponseGenerator handler = new ResponseGenerator(writer);

        // Generate the response for the given request
        handler.generateResponse(request);

        // Verify if the setUserInfo method is called with the expected parameters
        verify(user).updateInfo(anyString(), anyString(), anyString());

        // Verify if the flush method of the writer is called
        verify(writer).flush();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Test the user edit process when the user is not authorized.
     * This test verifies that the setUserInfo method is not called
     * and the response is generated correctly.
     * @throws IOException if there's an issue with the writer
     */
    @Test
    public void testWrongUser() throws IOException {
        // Set up the test environment for the unauthorized user edit
        try (MockedStatic<Handler_User> mockedHandlerUser = Mockito.mockStatic(Handler_User.class)) {
            prepareTestWrongUser(mockedHandlerUser);

            // Perform the actual test
            executeTestWrongUser();
        }
    }

    /**
     * Set up the test environment for the unauthorized user edit.
     * @param mockedHandlerUser the mocked Handler_User object
     */
    private void prepareTestWrongUser(MockedStatic<Handler_User> mockedHandlerUser) {
        // Configure the mocked Handler_User instance
        mockedHandlerUser.when(Handler_User::getInstance).thenReturn(handlerUser);

        // Set up the user authorization
        when(handlerUser.authorize(anyString())).thenReturn(user);
        when(user.getUsername()).thenReturn("altenhof");

        // Set request method, resource, and body
        request.setMethod("PUT");
        request.setResource("/users/kienboec");
        request.setBody("{\"Name\": \"Kienboeck\",  \"Bio\": \"me playin...\", \"Image\": \":-)\"}");
    }

    /**
     * Execute the unauthorized user edit test.
     * @throws IOException if there's an issue with the writer
     */
    private void executeTestWrongUser() throws IOException {
        // Create a ResponseGenerator object
        ResponseGenerator handler = new ResponseGenerator(writer);

        // Generate the response for the given request
        handler.generateResponse(request);

        // Verify if the setUserInfo method is not called
        verify(user, times(0)).updateInfo(anyString(), anyString(), anyString());

        // Verify if the flush method of the writer is called
        verify(writer).flush();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Test the card package creation process for an admin user.
     * This test verifies that the registerCard and createCardPackage methods are called
     * and the response is generated correctly.
     * @throws IOException if there's an issue with the writer
     */
    @Test
    public void testPackage() throws IOException {
        // Set up the test environment for the card package creation
        try (MockedStatic<Handler_User> mockedHandlerUser = Mockito.mockStatic(Handler_User.class);
             MockedStatic<Handler_Card> mockedHandlerCard = Mockito.mockStatic(Handler_Card.class)) {

            prepareTestPackage(mockedHandlerUser, mockedHandlerCard);

            // Perform the actual test
            executeTestPackage();
        }
    }

    /**
     * Set up the test environment for the card package creation.
     * @param mockedHandlerUser the mocked Handler_User object
     * @param mockedHandlerCard the mocked Handler_Card object
     */
    private void prepareTestPackage(MockedStatic<Handler_User> mockedHandlerUser, MockedStatic<Handler_Card> mockedHandlerCard) {
        // Configure the mocked Handler_User and Handler_Card instances
        mockedHandlerUser.when(Handler_User::getInstance).thenReturn(handlerUser);
        mockedHandlerCard.when(Handler_Card::getInstance).thenReturn(handlerCard);

        // Set up admin and card registration
        when(handlerUser.roleVerification(anyString())).thenReturn(true);
        when(handlerCard.logCard(anyString(), anyString(), anyFloat())).thenReturn(true);
        when(handlerCard.createCardPackage(anyList())).thenReturn(true);

        // Set request method, resource, and body
        request.setMethod("POST");
        request.setResource("/packages");
        request.setBody("[{\"Id\":\"b017ee50-1c14-44e2-bfd6-2c0c5653a37c\", \"Name\":\"WaterGoblin\", \"Damage\": 11.0}, {\"Id\":\"d04b736a-e874-4137-b191-638e0ff3b4e7\", \"Name\":\"Dragon\", \"Damage\": 70.0}, {\"Id\":\"88221cfe-1f84-41b9-8152-8e36c6a354de\", \"Name\":\"WaterSpell\", \"Damage\": 22.0}, {\"Id\":\"1d3f175b-c067-4359-989d-96562bfa382c\", \"Name\":\"Ork\", \"Damage\": 40.0}, {\"Id\":\"171f6076-4eb5-4a7d-b3f2-2d650cc3d237\", \"Name\":\"RegularSpell\", \"Damage\": 28.0}]");
    }

    /**
     * Execute the card package creation test.
     * @throws IOException if there's an issue with the writer
     */
    private void executeTestPackage() throws IOException {
        // Create a ResponseGenerator object
        ResponseGenerator handler = new ResponseGenerator(writer);

        // Generate the response for the given request
        handler.generateResponse(request);

        // Verify if the registerCard method is called 5 times
        verify(handlerCard, times(5)).logCard(anyString(), anyString(), anyFloat());

        // Verify if the createCardPackage method is called
        verify(handlerCard).createCardPackage(anyList());

        // Verify if the flush method of the writer is called
        verify(writer).flush();
    }
}