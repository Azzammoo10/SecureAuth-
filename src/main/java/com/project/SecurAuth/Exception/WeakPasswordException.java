package com.project.SecurAuth.Exception;

public class WeakPasswordException extends RuntimeException{
    public WeakPasswordException(String message) {
        super(message);
    }

}
