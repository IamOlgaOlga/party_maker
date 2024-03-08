package uk.co.imperatives.exercise.exception;

/**
 * Exercise service exception for case if an object is already exists.
 */
public class ExerciseAlreadyExistsException extends RuntimeException {

    public ExerciseAlreadyExistsException(String message) {
        super(message);
    }
}
