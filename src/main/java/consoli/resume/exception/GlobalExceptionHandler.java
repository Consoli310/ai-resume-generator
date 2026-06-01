package consoli.resume.exception;

import consoli.resume.dto.response.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            ResourceNotFoundException.class
    )
    public ResponseEntity<ErrorResponseDTO>
    handleNotFound(

            ResourceNotFoundException ex

    ) {

        ErrorResponseDTO error =
                new ErrorResponseDTO(
                        LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        ex.getMessage()
                );

        return ResponseEntity
                .status(
                        HttpStatus.NOT_FOUND
                )
                .body(error);
    }

    @ExceptionHandler(
            AIIntegrationException.class
    )
    public ResponseEntity<ErrorResponseDTO>
    handleAI(

            AIIntegrationException ex

    ) {

        ErrorResponseDTO error =
                new ErrorResponseDTO(
                        LocalDateTime.now(),
                        HttpStatus.BAD_GATEWAY.value(),
                        "AI Integration Error",
                        ex.getMessage()
                );

        return ResponseEntity
                .status(
                        HttpStatus.BAD_GATEWAY
                )
                .body(error);
    }

    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<Map<String, Object>>
    handleValidation(

            MethodArgumentNotValidException ex

    ) {

        Map<String, String>
                errors =
                new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->

                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )

                );

        Map<String, Object>
                response =
                new HashMap<>();

        response.put(
                "timestamp",
                LocalDateTime.now()
        );

        response.put(
                "status",
                HttpStatus.BAD_REQUEST.value()
        );

        response.put(
                "error",
                "Validation Error"
        );

        response.put(
                "fields",
                errors
        );

        return ResponseEntity
                .status(
                        HttpStatus.BAD_REQUEST
                )
                .body(
                        response
                );
    }

    @ExceptionHandler(
            AuthenticationException.class
    )
    public ResponseEntity<ErrorResponseDTO>
    handleAuthentication(

            AuthenticationException ex

    ) {

        return ResponseEntity
                .status(
                        HttpStatus.UNAUTHORIZED
                )
                .body(

                        new ErrorResponseDTO(

                                LocalDateTime.now(),
                                401,
                                "Unauthorized",
                                ex.getMessage()

                        )

                );
    }

    @ExceptionHandler(
            ConflictException.class
    )
    public ResponseEntity<ErrorResponseDTO>
    handleConflict(

            ConflictException ex

    ) {

        return ResponseEntity
                .status(
                        HttpStatus.CONFLICT
                )
                .body(

                        new ErrorResponseDTO(

                                LocalDateTime.now(),
                                409,
                                "Conflict",
                                ex.getMessage()

                        )

                );
    }

    @ExceptionHandler(
            Exception.class
    )
    public ResponseEntity<ErrorResponseDTO>
    handleGeneric(

            Exception ex

    ) {

        ErrorResponseDTO error =
                new ErrorResponseDTO(
                        LocalDateTime.now(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR
                                .getReasonPhrase(),
                        ex.getMessage()
                );

        return ResponseEntity
                .status(
                        HttpStatus.INTERNAL_SERVER_ERROR
                )
                .body(error);
    }
}