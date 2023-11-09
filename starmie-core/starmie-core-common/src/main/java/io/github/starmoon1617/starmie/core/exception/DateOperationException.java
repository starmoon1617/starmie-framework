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

    /**
     * Constructs a new Date Operation exception with {@code null} as its detail
     * message. The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public DateOperationException() {
        super();
    }

    /**
     * Constructs a new Date Operation exception with the specified detail
     * message. The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     *
     * @param message
     *            the detail message. The detail message is saved for later
     *            retrieval by the {@link #getMessage()} method.
     */
    public DateOperationException(String message) {
        super(message);
    }

    /**
     * Constructs a new Date Operation exception with the specified detail
     * message and cause.
     * <p>
     * Note that the detail message associated with {@code cause} is <i>not</i>
     * automatically incorporated in this Date Operation exception's detail
     * message.
     *
     * @param message
     *            the detail message (which is saved for later retrieval by the
     *            {@link #getMessage()} method).
     * @param cause
     *            the cause (which is saved for later retrieval by the
     *            {@link #getCause()} method). (A {@code null} value is
     *            permitted, and indicates that the cause is nonexistent or
     *            unknown.)
     */
    public DateOperationException(String message, Throwable cause) {
        super(message, cause);
    }

}
