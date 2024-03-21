package th.co.cdgs.employee;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import th.co.cdgs.department.Department;

@QuarkusTest
class VersionTest {


        @Test
        void updateBruceWayneToJackNapierShouldGetJackNapier() throws JsonProcessingException {
                ObjectMapper objectMapper = new ObjectMapper();
                Response response = given().when().get("/employee/2").then().extract().response();
                Employee bruch = objectMapper.readValue(response.getBody().asString(),
                                Employee.class);
                assertEquals("Bruce", bruch.getFirstName());
                assertEquals("Wayne", bruch.getLastName());
                bruch.setFirstName("Jack");
                bruch.setLastName("Napier");
                given().when().body(objectMapper.writeValueAsString(bruch))
                                .contentType("application/json").put("/employee/2").then()
                                .statusCode(200).body(containsString("Jack"));
                given().when().get("/employee/2").then().statusCode(200).body(containsString("Jack"));
        }

        @Test
        void updateBruceWayneToJackNapierShouldOptimisticLockException() throws JsonProcessingException {
                ObjectMapper objectMapper = new ObjectMapper();
                Response bruceResponse =
                                given().when().get("/employee/2").then().extract().response();
                Employee bruce = objectMapper.readValue(bruceResponse.getBody().asString(),
                                Employee.class);
                bruce.setFirstName("Jack");
                bruce.setLastName("Napier");
                bruce.setVersion(0);
                Response response = given().when().body(objectMapper.writeValueAsString(bruce))
                                .contentType("application/json").put("/employee/2").then().extract()
                                .response();
                assertEquals(500, response.statusCode());
                assertEquals("ไม่สามารถแก้ไขข้อมูลได้ เนื่องจาก ข้อมูลถูกแก้ไขด้วยผู้อื่น",
                                response.body().jsonPath().getString("message"));
        }

        @Test
        void changeDepartmentClarkToMavelShouldBeGetMavel() throws JsonProcessingException {
                ObjectMapper objectMapper = new ObjectMapper();
                Employee clark = new Employee();
                clark.setVersion(0);
                clark.setDepartment(new Department(1, "Mavel"));
                given().when().body(objectMapper.writeValueAsString(clark))
                                .contentType("application/json").patch("/employee/6").then()
                                .statusCode(200).body(containsString("Mavel"));
                given().when().get("/employee/6").then().statusCode(200)
                                .body(containsString("Mavel"));
        }
        

        @Test
        void changeDepartmentBruceToMavelShouldOptimisticLockException() throws JsonProcessingException {
                ObjectMapper objectMapper = new ObjectMapper();
                Employee bruce = new Employee();
                bruce.setVersion(0);
                bruce.setDepartment(new Department(1, "Mavel"));
                Response response = given().when().body(objectMapper.writeValueAsString(bruce))
                                .contentType("application/json").patch("/employee/2").then().extract()
                                .response();
                assertEquals(500, response.statusCode());
                assertEquals("ไม่สามารถแก้ไขข้อมูลได้ เนื่องจาก ข้อมูลถูกแก้ไขด้วยผู้อื่น",
                                response.body().jsonPath().getString("message"));
        }

}
