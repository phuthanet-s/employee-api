package th.co.cdgs.department;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class DepartmentEndpointTest {


    @Test
    void deleteEmployeeByDepartmentMavelButEmployeeTonyShouldBeFound() {
        given().when().delete("/department/1").then().statusCode(200);
        given().when().get("/department/1").then().statusCode(200).body(containsString("Mavel"));
        given().when().get("/employee/3").then().statusCode(404);
        given().when().get("/employee/1").then().statusCode(200).body(containsString("Tony"));
    }


    @Test
    void deleteEmployeeByDepartmentDCShouldBeEmpty() {
        given().when().delete("/department/2").then().statusCode(200);
        given().when().get("/department/2").then().statusCode(404);
        given().when().get("/employee/search?department=2").then().statusCode(200)
                .body(containsString("[]"));
    }
}

