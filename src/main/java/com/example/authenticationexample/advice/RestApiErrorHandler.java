package com.example.authenticationexample.advice;

import com.example.authenticationexample.dto.other.ExceptionResult;
import com.example.authenticationexample.entity.UserTokenEntity;
import com.example.authenticationexample.exception.EmailNotFound;
import com.example.authenticationexample.exception.InsufficientAuthentication;
import com.example.authenticationexample.exception.InvalidRefreshToken;
import com.example.authenticationexample.exception.UserAlreadyExist;
import com.example.authenticationexample.utils.Utils;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class RestApiErrorHandler extends Utils {
    private static final Logger log = LoggerFactory.getLogger(RestApiErrorHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResult> handleGeneralError(Exception e, HttpServletRequest req) {
        ExceptionResult res = new ExceptionResult(getTimestamp(), req.getRequestURI(), "", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ExceptionResult> handleDtoValidationError(BindException e, HttpServletRequest req) {
        ExceptionResult res = new ExceptionResult(getTimestamp(), req.getRequestURI(), e.getBindingResult()
                                                                                        .getAllErrors()
                                                                                        .get(0)
                                                                                        .getDefaultMessage(), HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(EmailNotFound.class)
    public ResponseEntity<ExceptionResult> handleEmailNotFound(EmailNotFound e, HttpServletRequest req) {
        ExceptionResult res = new ExceptionResult(getTimestamp(), req.getRequestURI(), e.getMessage(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserAlreadyExist.class)
    public ResponseEntity<ExceptionResult> handleEmailConflict(UserAlreadyExist e, HttpServletRequest req) {
        ExceptionResult res = new ExceptionResult(getTimestamp(), req.getRequestURI(), e.getMessage(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidRefreshToken.class)
    public ResponseEntity<ExceptionResult> handleInvalidRefreshToken(InvalidRefreshToken e, HttpServletRequest req) {
        ExceptionResult res = new ExceptionResult(getTimestamp(), req.getRequestURI(), e.getMessage(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InsufficientAuthentication.class)
    public ResponseEntity<ExceptionResult> handleInsufficientAuthentication(InsufficientAuthentication e, HttpServletRequest req) {
        ExceptionResult res = new ExceptionResult(getTimestamp(), req.getRequestURI(), e.getMessage(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
    }
}
