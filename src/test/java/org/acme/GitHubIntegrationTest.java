package org.acme;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

// Happy path, assumptions: proper username was given, rate limit for GitHub REST API was not exceeded, given username has at least 1 repo
@QuarkusTest
public class GitHubIntegrationTest {
    private static final String GITHUB_USERNAME = "torvalds"; // Public GitHub username

    @Test
    void testGithubReposIntegration() {
        Response response = given()
                .when()
                .get("/github/" + GITHUB_USERNAME)
                .then()
                .statusCode(200)
                .body("$", not(empty()))
                .body("[0].ownerLogin", not(emptyOrNullString()))
                .body("[0].name", not(emptyOrNullString()))
                .body("[0].branches", notNullValue())
                .extract()
                .response();

        var repositories = response.jsonPath().getList("$");
        assert !repositories.isEmpty() : "Expected at least one repository";

        var branches = response.jsonPath().getList("[0].branches");

        assert branches != null : "Branches should not be null";
        if (!branches.isEmpty()) {
            assert response.jsonPath().getString("[0].branches[0].name") != null : "Branch should have a name";
            assert response.jsonPath().getString("[0].branches[0].sha") != null : "Branch should have a sha";
        }

        System.out.println("Integration test passed successfully!");
    }
}