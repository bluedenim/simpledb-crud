package org.van.simpledbui.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * HTTP response-enabled exception
 *
 * Created by vly on 9/20/2015.
 */
@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="Illegal arguments specified.")
public class IllegalArgumentWebException extends RuntimeException {

    public IllegalArgumentWebException(String message, Throwable t) {
        super(message, t);
    }

    public IllegalArgumentWebException(String message) {
        super(message);
    }
}
