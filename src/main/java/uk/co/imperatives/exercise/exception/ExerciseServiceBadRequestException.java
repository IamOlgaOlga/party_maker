package uk.co.imperatives.exercise.exception;

/**
 * Exercise service exception
 */
public class ExerciseServiceBadRequestException extends RuntimeException {
    public ExerciseServiceBadRequestException(String message) {
        super(message);
    }
}
