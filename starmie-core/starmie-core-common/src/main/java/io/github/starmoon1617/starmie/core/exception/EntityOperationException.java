/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.exception;

/**
 * Exception for entity operation
 * 
 * @date 2023-10-09
 * @author Nathan Liao
 */
public class EntityOperationException extends RuntimeException {

    private static final long serialVersionUID = -7913881018274515153L;

    public EntityOperationException() {
        super();
    }

    public EntityOperationException(String message) {
        super(message);
    }

    public EntityOperationException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
