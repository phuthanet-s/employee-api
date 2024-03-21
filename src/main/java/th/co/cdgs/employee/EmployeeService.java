package th.co.cdgs.employee;

import java.util.List;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

@ApplicationScoped
public class EmployeeService {
	@Inject
	EmployeeService employeeService;
	@Inject
	EntityManager entityManager;

	@Transactional
	public boolean deleteByDepartment(Integer department) {
		boolean hasError = false;
		String jpql = "from Employee e where e.department.code = :department ";
		Query query = entityManager.createQuery(jpql, Employee.class);
		query.setParameter("department", department);
		List<Employee> emps = query.getResultList();
		for (Employee emp : emps) {
			try {
				employeeService.deleteById(emp.getId());
			} catch (Exception e) {
				Log.error(e);
				hasError = true;
			}
		}
		return hasError;
	}

	@Transactional(value = TxType.REQUIRES_NEW)
	public void deleteById(Integer id) {
		Employee entity = entityManager.find(Employee.class, id);
		entityManager.remove(entity);
	}

	@Transactional
	public Employee changeDepartment(Employee entity, Employee employee) {
		entity.setDepartment(employee.getDepartment());
		entity.setVersion(employee.getVersion());
		return entityManager.merge(entity);
	}

}
