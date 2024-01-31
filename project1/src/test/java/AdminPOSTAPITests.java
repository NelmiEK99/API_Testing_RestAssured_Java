import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class AdminPOSTAPITests {

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "http://localhost:7081/api/books"; // Set your API base URL
        RestAssured.authentication = RestAssured.basic("admin", "password");
    }

    @Test
    public void testAdminCreateBookWithMandatoryParameters() {
        String requestBody = "{\"title\":\"Sherlock Holmes in Washington\",\"author\":\"Dun Colegate\"}";

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post();

        response.then().log().all();
        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();

        // Assertions
        assertEquals(statusCode, 201, "Expected status code 201");
        response.then().assertThat().contentType(ContentType.JSON);
    }

    @Test
    public void testAdminCreateBookWithOptionalID() {
        String requestBody = "{\"id\":80,\"title\":\"Ambulance, The\",\"author\":\"Lida Leadstone\"}";

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post();

        response.then().log().all();
        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();

        // Assertions
        assertEquals(statusCode, 201, "Expected status code 201");
        response.then().assertThat().contentType(ContentType.JSON);
        assertTrue(responseBody.contains("{\"id\":80,\"title\":\"Ambulance, The\",\"author\":\"Lida Leadstone\"}"),
                "Response body should contain expected content");

    }

    @Test
    public void testAdminCreateBookWithInvalidMandatoryParameters() {
        String requestBody = "{\"title\":\"\",\"author\":\"\"}";

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post();

        int statusCode = response.getStatusCode();
        response.then().log().all();

        // Assertions
        // Assuming a 400 Bad Request for an invalid author and title parameters
        assertEquals(statusCode, 400, "Expected status code 400");
    }

    @Test
    public void testAdminCreateBookWithDuplicateID() {
        String requestBody1 =  "{\"id\":20,\"title\":\"The Castle of Fu Manchu\",\"author\":\"Gina Bruggeman\"}";
        String requestBody2 =  "{\"id\":20,\"title\":\"Nude Bomb, The\",\"author\":\"Cariotta Gamble\"}";

        Response response1 = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody1)
                .post();

        Response response2 = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody2)
                .post();

        // Assertions for the first request
        response1.then().statusCode(201);
        response1.then().assertThat().contentType(ContentType.JSON);

        // Assertions for the second request
        response2.then().statusCode(208);  // Assuming a 208 Already Reported  Error for duplicate ID
    }

    @Test
    public void testAdminCreateBookWithDuplicateData() {
        String requestBody =  "{\"title\":\"Chattahoochee\",\"author\":\"Allin Cannicott\"}";

        Response response1 = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post();

        Response response2 = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post();

        // Assertions
        response1.then().statusCode(201);
        response1.then().assertThat().contentType(ContentType.JSON);
        response2.then().statusCode(208); // Assuming a 208 Already Reported  Error for duplicate Author
    }

    @Test
    public void testAdminCreateBookWithAuthorizationData() {
        String requestBody = "{\"title\":\"Lover's Book\",\"author\":\"John Doe\"}";

        Response response = RestAssured.given()
                .auth().basic("admin", "123")
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post();

        response.then().log().all();

        // Assertions
        response.then().statusCode(401); //Expect error 401 for not authorized to create the book

    }
}

//Bugs Founded
//testAdminCreateBookWithOptionalID(): When you provide an ID number for optional attribute, system not take that
//                                    as a input. System automatically increasingly adding the ID number. Whether
//                                    it provided or not.
//testAdminCreateBookWithInvalidMandatoryParameters(): Even you haven't provide mandatory parameters, system return
//                                                    success codes. Even they are null system get it as a input.