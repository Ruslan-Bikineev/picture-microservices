package edu.school21.exception;

public class UnauthorizedCommentDeletionException extends RuntimeException {

    public UnauthorizedCommentDeletionException(String message) {
        super(message);
    }
}
