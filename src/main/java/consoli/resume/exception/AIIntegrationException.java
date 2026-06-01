package consoli.resume.exception;

public class AIIntegrationException
        extends RuntimeException {

    public AIIntegrationException(
            String message,
            Throwable cause
    ) {
        super(
                message,
                cause
        );
    }
}