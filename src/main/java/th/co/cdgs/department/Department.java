package th.co.cdgs.department;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "department")
public class Department extends BaseDepartment {

	public Department() {

	}

	public Department(Integer code, String name) {
		super.setCode(code);
		super.setName(name);
	}

}
