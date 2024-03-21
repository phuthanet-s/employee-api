package th.co.cdgs.department;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import th.co.cdgs.employee.EmployeeService;

@Path("department")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class DepartmentResource {
    @Inject
    EmployeeService employeeService;

    @Inject
    EntityManager entityManager;

    @GET
    public List<Department> get() {
        return entityManager.createQuery("from Department", Department.class).getResultList();
    }

    @GET
    @Path("emp")
    public List<DepartmentEmp> getDepartmentEmp() {
        return entityManager.createQuery("from DepartmentEmp", DepartmentEmp.class).getResultList();
    }

    @GET
    @Path("{id}")
    public Department getSingle(Integer id) {
        Department entity = entityManager.find(Department.class, id);

        if (entity == null) {
            throw new WebApplicationException("employee with id of " + id + " does not exist.",
                    Status.NOT_FOUND);
        }
        return entity;
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(Integer id) {
        Department entity = entityManager.getReference(Department.class, id);
        if (!employeeService.deleteByDepartment(entity.getCode())) {
            entityManager.remove(entity);
        }
        return Response.ok().build();
    }

}
