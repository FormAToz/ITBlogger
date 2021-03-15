package main.exception;

import main.Main;
import main.api.response.result.ErrorResultResponse;
import main.api.response.result.ResultResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.WebUtils;

import javax.mail.MessagingException;
import java.io.IOException;

/**
 * Класс обработки исключений приложения
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    private static final Marker MARKER = MarkerManager.getMarker("APP_INFO");

    /**
     * Метод обработки исключений в рамках этой службы
     */
    @ExceptionHandler({
            ContentNotFoundException.class,
            UsernameNotFoundException.class,
            InvalidParameterException.class,
            MessagingException.class,
            IOException.class,
            AuthenticationException.class})
    public final ResponseEntity<ResultResponse> handleException(Exception ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();

        if (ex instanceof ContentNotFoundException) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            ContentNotFoundException cnpe = (ContentNotFoundException) ex;

            LOGGER.info(MARKER, ex.getMessage());
            return handleContentNotFoundException(cnpe, headers, status, request);

        } else if (ex instanceof UsernameNotFoundException) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            UsernameNotFoundException unfe = (UsernameNotFoundException) ex;

            LOGGER.info(MARKER, ex.getMessage());
            return handleUsernameNotFoundException(unfe, headers, status, request);

        } else if (ex instanceof InvalidParameterException) {
            HttpStatus status = HttpStatus.OK;
            InvalidParameterException ipe = (InvalidParameterException) ex;

            return handleInvalidParameterException(ipe, headers, status, request);

        } else if (ex instanceof MessagingException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            MessagingException me = (MessagingException) ex;

            return handleMessagingException(me, headers, status, request);

        } else if (ex instanceof IOException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            IOException ioex = (IOException) ex;

            return handleIOException(ioex, headers, status, request);

        } else if (ex instanceof AuthenticationException) {
            HttpStatus status = HttpStatus.OK;
            AuthenticationException authEx = (AuthenticationException) ex;

            return handleAuthenticationException(authEx, headers, status, request);
        } else {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

            return handleExceptionInternal(ex, null, headers, status, request);
        }
    }

    /**
     * Метод настройки ответа для ContentNotPresentException
     */
    protected ResponseEntity<ResultResponse> handleContentNotFoundException(ContentNotFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, new ErrorResultResponse(false, ex.getMessage()), headers, status, request);
    }

    /**
     * Метод настройки ответа для UsernameNotFoundException
     */
    private ResponseEntity<ResultResponse> handleUsernameNotFoundException(UsernameNotFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, new ErrorResultResponse(false, "user", ex.getMessage()), headers, status, request);
    }

    /**
     * Метод настройки ответа для InvalidParameterException
     */
    private ResponseEntity<ResultResponse> handleInvalidParameterException(InvalidParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, new ErrorResultResponse(false, ex.getType(), ex.getMessage()), headers, status, request);
    }

    /**
     * Метод настройки ответа для MessagingException
     */
    private ResponseEntity<ResultResponse> handleMessagingException(MessagingException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, new ErrorResultResponse(false, ex.getMessage()), headers, status, request);
    }

    /**
     * Метод настройки ответа для IOException
     */
    private ResponseEntity<ResultResponse> handleIOException(IOException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, new ErrorResultResponse(false, ex.getMessage()), headers, status, request);
    }

    /**
     * Метод настройки ответа для AuthenticationException
     */
    private ResponseEntity<ResultResponse> handleAuthenticationException(AuthenticationException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, new ErrorResultResponse(false, ex.getMessage()), headers, status, request);
    }

    /**
     * Метод настройки тела ответа для всех типов исключений
     */
    protected ResponseEntity<ResultResponse> handleExceptionInternal(Exception ex, ResultResponse body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }

        return new ResponseEntity<>(body, headers, status);
    }
}
