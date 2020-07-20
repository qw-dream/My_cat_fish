package com.qw.tomcat.exception;

public class UriUnuniqueException extends RuntimeException{

    public UriUnuniqueException() {
    }

    public UriUnuniqueException(String message) {
        super(message);
    }
}