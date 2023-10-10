/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.exception;

/**
 * Exception for date operation
 * 
 * @date 2023-10-10
 * @author Nathan Liao
 */
public class DateOperationException extends RuntimeException {

    private static final long serialVersionUID = -8009155055085682399L;
    
    public DateOperationException() {
        super();
    }

    public DateOperationException(String message) {
        super(message);
    }

    public DateOperationException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
