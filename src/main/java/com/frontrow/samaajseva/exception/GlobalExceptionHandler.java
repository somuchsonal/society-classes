package com.frontrow.samaajseva.exception;

import com.frontrow.samaajseva.pojo.response.ErrorResponse;
import com.frontrow.samaajseva.pojo.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.ws.rs.InternalServerErrorException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InternalServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Response<?> handleInternalServerException(Exception e){
        log.error("[InternalServerError]: ", e);
        CustomException customException = new CustomException(ErrorCodes.INTERNAL_SERVER_ERROR);
        return buildResponse(customException, "Some error occurred!");
    }

    @ExceptionHandler(CustomException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Response<?> handleCustomException(CustomException ce){
        log.error("[CustomException]: ", ce);
        return buildResponse(ce, CustomException.class.getSimpleName(), ce.getMessage());
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Response<?> handleSQLIntegrityConstraintViolationException(Exception e){
        log.error("[SQLIntegrityConstraintViolationException]: ", e);
        CustomException customException = new CustomException(ErrorCodes.SQL_EXCEPTION);
        return buildResponse(customException, e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Response<?> handleConstraintViolationException(Exception e){
        log.error("[ConstraintViolationException]: ", e);
        CustomException customException = new CustomException(ErrorCodes.CONSTRAINT_VIOLATION_EXCEPTION);
        return buildResponse(customException, e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Response<?> handleDataIntegrityViolationException(Exception e){
        log.error("[DataIntegrityViolationException]: ", e);
        CustomException customException = new CustomException(ErrorCodes.DATA_INTEGRITY_VIOLATION_EXCEPTION);
        return buildResponse(customException, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Response<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        log.error("[MethodArgumentNotValidException]: ", ex);
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }

        StringBuilder errorMessage = new StringBuilder();
        for (int i = 0; i < errors.size(); i++) {
            errorMessage.append(i + 1).append(") ").append(errors.get(i)).append(" ");
        }
        errorMessage = new StringBuilder(errorMessage.substring(0, errorMessage.length() - 1));
        CustomException customException = new CustomException(ErrorCodes.METHOD_ARGUMENT_NOT_VALID);
        return buildResponse(customException, "Validation error(s): " + errorMessage);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Response<?> handleException(Exception e){
        log.error("[Exception]: ", e);
        CustomException customException = new CustomException(ErrorCodes.EXCEPTION);
        return buildResponse(customException, e.getMessage());
    }

    private Response<?> buildResponse(CustomException customException, String message) {
        return this.buildResponse(customException, customException.getMessage(), message);
    }

    private Response<?> buildResponse(CustomException customException, String type, String message) {
        ErrorResponse errorResponse = new ErrorResponse(customException.getCode(), type, message);
        return new Response<>(false, null, errorResponse);
    }
}

