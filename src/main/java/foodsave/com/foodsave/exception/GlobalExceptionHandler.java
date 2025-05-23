package foodsave.com.foodsave.exception;

import foodsave.com.foodsave.monitoring.ServerMonitoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.io.PrintWriter;
import java.io.StringWriter;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private final ServerMonitoringService monitoringService;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllUncaughtException(Exception exception) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String stackTrace = sw.toString();

        // Отправка уведомления о необработанной ошибке
        monitoringService.logError(exception.getMessage(), stackTrace);

        return ResponseEntity
                .internalServerError()
                .body(new ErrorResponse(
                        "An unexpected error occurred",
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException exception) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String stackTrace = sw.toString();

        // Отправка уведомления о runtime ошибке
        monitoringService.logError(exception.getMessage(), stackTrace);

        return ResponseEntity
                .internalServerError()
                .body(new ErrorResponse(
                        "A runtime error occurred",
                        exception.getMessage()
                ));
    }
}

class ErrorResponse {
    private final String message;
    private final String details;

    public ErrorResponse(String message, String details) {
        this.message = message;
        this.details = details;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }
}