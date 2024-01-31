import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class APITests {

    @BeforeClass
    public void setup() {
        try {
            RestAssured.baseURI = ConfigLoader.BASE_URL;
            RestAssured.authentication = RestAssured.basic(ConfigLoader.USER_USERNAME, ConfigLoader.USER_PASSWORD);
        } catch (Exception e) {
            // Handle exception, log it, and perform necessary actions
            System.out.println("Exception during setup: " + e.getMessage());
        }
    }

    @Test
    public void testCreateBookWithMandatoryParameters() {
        String requestBody = "{\"title\":\"Sherlock Holmes in Washington\",\"author\":\"Dun Colegate\"}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post(ConfigLoader.API_ENDPOINT);

        response.then().log().all();
        int statusCode = response.getStatusCode();

        // Assertions
        assertEquals(statusCode, 201, "Expected status code 201");
        response.then().assertThat().contentType(ContentType.JSON);
    }

    @Test
    public void testCreateBookWithOptionalID() {
        String requestBody = "{\"id\":80,\"title\":\"Ambulance, The\",\"author\":\"Lida Leadstone\"}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post(ConfigLoader.API_ENDPOINT);

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
    public void testCreateBookWithNonIntegerID() {
        String requestBody = "{\"id\":\"75\",\"title\":\"Friends\",\"author\":\"Mattew Perry\"}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post(ConfigLoader.API_ENDPOINT);

        response.then().log().all();
        int statusCode = response.getStatusCode();

        // Assertions
        assertEquals(statusCode, 400, "Expected status code 400");
    }

    @Test
    public void testCreateBookWithEmptyMandatoryParameters() {
        String requestBody = "{\"title\":\"\",\"author\":\"\"}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post(ConfigLoader.API_ENDPOINT);

        int statusCode = response.getStatusCode();
        response.then().log().all();

        // Assertions
        assertEquals(statusCode, 400, "Expected status code 400");
    }

    @Test
    public void testCreateBookWithInvalidMandatoryParameters() {
        String requestBody = "{\"title\":5093,\"author\":7041}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post(ConfigLoader.API_ENDPOINT);

        int statusCode = response.getStatusCode();
        response.then().log().all();

        // Assertions
        assertEquals(statusCode, 400, "Expected status code 400");
    }

    @Test
    public void testCreateBookWithDuplicateID() {
        String requestBody1 = "{\"id\":20,\"title\":\"The Castle of Fu Manchu\",\"author\":\"Gina Bruggeman\"}";
        String requestBody2 = "{\"id\":20,\"title\":\"Nude Bomb, The\",\"author\":\"Cariotta Gamble\"}";

        Response response1 = given()
                .contentType(ContentType.JSON)
                .body(requestBody1)
                .post(ConfigLoader.API_ENDPOINT);

        Response response2 = given()
                .contentType(ContentType.JSON)
                .body(requestBody2)
                .post(ConfigLoader.API_ENDPOINT);

        response2.then().log().all();
        int statusCode = response2.getStatusCode();

        // Assertions
        response1.then().statusCode(201);
        response1.then().assertThat().contentType(ContentType.JSON);

        // Assuming a 208 Already Reported  Error for duplicate data
        assertEquals(statusCode, 208, "Expected status code 208");
    }

    @Test
    public void testCreateBookWithDuplicateData() {
        String requestBody = "{\"title\":\"Chattahoochee\",\"author\":\"Allin Cannicott\"}";

        Response response1 = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post(ConfigLoader.API_ENDPOINT);

        Response response2 = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post(ConfigLoader.API_ENDPOINT);

        response2.then().log().all();
        int statusCode = response2.getStatusCode();

        // Assertions
        response1.then().statusCode(201);
        response1.then().assertThat().contentType(ContentType.JSON);

        // Assuming a 208 Already Reported  Error for duplicate data
        assertEquals(statusCode, 208, "Expected status code 208");
    }

    @Test
    public void testExceedMaximumTitleLength() {
        String maxLengthTitle = "In a quaint village nestled between rolling hills, the townspeople gather " +
                "at the local market every Saturday morning. Vendors display their colorful produce, fragrant " +
                "flowers, and handmade crafts. Families stroll along the cobblestone streets, enjoying the lively " +
                "atmosphere. The aroma of freshly baked bread wafts through the air, enticing passersby. " +
                "Children laugh and play in the nearby park, while musicians provide a cheerful soundtrack." +
                " The market is a place where the community comes alive, sharing stories, traditions, " +
                "and the simple joys of life. It's a cherished weekly event that brings people " +
                "together in this idyllic setting.";

        String requestBody = "{\"title\":\"" + maxLengthTitle + "\",\"author\":\"Sample Author\"}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post(ConfigLoader.API_ENDPOINT);

        response.then().log().all();
        response.then().statusCode(400);
    }

    @Test
    public void testExceedMaximumAuthorLength() {
        String maxLengthAuthor = "In a quaint village nestled between rolling hills, the townspeople gather " +
                "at the local market every Saturday morning. Vendors display their colorful produce, fragrant " +
                "flowers, and handmade crafts. Families stroll along the cobblestone streets, enjoying the lively " +
                "atmosphere. The aroma of freshly baked bread wafts through the air, enticing passersby. " +
                "Children laugh and play in the nearby park, while musicians provide a cheerful soundtrack." +
                " The market is a place where the community comes alive, sharing stories, traditions, " +
                "and the simple joys of life. It's a cherished weekly event that brings people " +
                "together in this idyllic setting.";
        // The above author exceeds the maximum allowed character limit.

        String requestBody = "{\"title\":\"Sample Book\",\"author\":\"" + maxLengthAuthor + "\"}";

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post(ConfigLoader.API_ENDPOINT);

        response.then().log().all();
        response.then().statusCode(400);
    }


    @Test
    public void testCreateBookWithInvalidUser() {
        String requestBody = "{\"title\":\"Think Like a Monk\",\"author\":\"Jay Shetty\"}";

        Response response = RestAssured.given()
                .auth().basic("dev", "123")
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post(ConfigLoader.API_ENDPOINT);

        response.then().log().all();

        // Assertions
        response.then().statusCode(401); //Expect error 401 for not authorized to create the book

    }
}

//Bugs Founded
//testAdminCreateBookWithOptionalID(): When you provide an ID number for optional attribute, system not take that
//                                    as an input. System automatically increasingly adding the ID number. Whether
//                                    it provided or not.
//testAdminCreateBookWithEmptyMandatoryParameters(): Even you haven't provide mandatory parameters, system return
//                                                    success codes. Even they are null system get it as a input.
//testCreateBookWithNonIntegerID(): When you provide invalid id which non integer, string still the system accept
//                                  the request without giving error, and create the book with auto generated id number.
//testCreateBookWithInvalidMandatoryParameters():Even you provide integers for mandatory parameters, system return
//                                                    success codes.