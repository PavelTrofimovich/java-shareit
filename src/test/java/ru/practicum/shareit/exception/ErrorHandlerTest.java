package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.exception.exceptions.*;
import ru.practicum.shareit.exception.model.ErrorResponse;

class ErrorHandlerTest {

    private final ErrorHandler handler = new ErrorHandler();

    @Test
    public void handleNotFoundExceptionTest() {
        NotFoundException e = new NotFoundException("NotFoundException");
        ErrorResponse errorResponse = handler.handleNotFoundException(e);
        Assertions.assertNotNull(errorResponse);
        Assertions.assertEquals("NotFoundException", errorResponse.getError());
    }

    @Test
    public void handleEmailExceptionTest() {
        EmailException e = new EmailException("EmailException");
        ResponseEntity responseEntity = handler.handleEmailException(e);
        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(409, responseEntity.getStatusCodeValue());
    }

    @Test
    public void userNotHaveThisItemTest() {
        UserNotHaveThisItem e = new UserNotHaveThisItem("UserNotHaveThisItem");
        ResponseEntity responseEntity = handler.userNotHaveThisItem(e);
        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(404, responseEntity.getStatusCodeValue());
    }

    @Test
    public void handleValidationExceptionTest() {
        ValidationException e = new ValidationException("ValidationException");
        ErrorResponse errorResponse = handler.handleValidationException(e);
        Assertions.assertNotNull(errorResponse);
        Assertions.assertEquals("ValidationException", errorResponse.getError());
    }

    @Test
    public void handleThrowableTest() {
        Throwable e = new Throwable();
        ErrorResponse errorResponse = handler.handleThrowable(e);
        Assertions.assertNotNull(errorResponse);
        Assertions.assertEquals("Произошла непредвиденная ошибка.", errorResponse.getError());
    }
}