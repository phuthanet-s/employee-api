package th.co.cdgs.mapper;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import io.quarkus.logging.Log;

@Provider
public class ErrorMapper implements ExceptionMapper<Exception> {

	private static final List<Class<?>> expectClazz =
			Arrays.asList(SQLIntegrityConstraintViolationException.class,
					OptimisticLockException.class, PersistenceException.class);

	private Throwable getExpectCause(Throwable throwable, List<Class<?>> classList) {
		Class<?> clazz = throwable.getClass();
		if (classList.contains(clazz)) {
			return throwable;
		} else {
			if (throwable.getCause() == null) {
				return throwable;
			}
			return getExpectCause(throwable.getCause(), classList);
		}
	}

	@Override
	public Response toResponse(Exception exception) {
		Map<String, String> messages = new HashMap<>();
		Throwable childCase = getExpectCause(exception, expectClazz);
		if (childCase instanceof SQLException) {
			String sqlState = ((SQLException) childCase).getSQLState();
			if("23503".equals(sqlState)){
				messages.put("message",
						"ไม่สามารถลบข้อมูลได้เนื้องจาก มีการนำไปใช้งานแล้ว");
			} else {
				Log.error("getErrorCode = " + ((SQLException) childCase).getErrorCode());
				messages.put("message",
						"SQLException code=" + ((SQLException) childCase).getErrorCode());
			}
		} else if (childCase instanceof jakarta.persistence.OptimisticLockException) {
			messages.put("message", "ไม่สามารถแก้ไขข้อมูลได้ เนื่องจาก ข้อมูลถูกแก้ไขด้วยผู้อื่น");
		} else if (childCase instanceof jakarta.persistence.PersistenceException && childCase
				.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
			messages.put("message", "Referential integrity constraint violation");
		} else {
			messages.put("message", exception.getMessage());
		}
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(messages).build();
	}

}
