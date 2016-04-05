package org.codeontology.exceptions;

public class NullTypeException extends RuntimeException {
    public NullTypeException() {
        super();
    }

    public NullTypeException(String message) {
        super(message);
    }
}
