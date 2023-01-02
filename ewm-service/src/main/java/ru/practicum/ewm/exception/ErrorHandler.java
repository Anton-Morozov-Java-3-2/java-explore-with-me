package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({EventDateNotValidException.class, EventStatusToEditException.class,
            MethodArgumentNotValidException.class, DataTimeFormatException.class,
            EventPublishDateNotValidException.class, DuplicateEventException.class, MethodArgumentNotValidException.class,
            MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(final Exception e) {
        String reason = "For the requested operation the conditions are not met";
        return new ApiError(e.getMessage(), reason, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({DuplicateRequestException.class, UserAccessException.class,
            RequestConfirmationNotValidException.class, EventStatusToPublishException.class,
            EventStatusToViewException.class,EventStatusToViewException.class, ReactionAlreadyExistException.class,
            ReactionNotAvailableException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleForbidden(final Exception e) {
        String reason = "For the requested operation the conditions are not met.";
        return new ApiError(e.getMessage(), reason, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({UserNotFoundException.class, CategoryNotFoundException.class, EventNotFoundException.class,
            RequestNotFoundException.class, CompilationNotFoundException.class, ReactionNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundRequest(final Exception e) {
        String reason = "The required object was not found.";
        return new ApiError(e.getMessage(), reason, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({UserEmailNotUniqueException.class, CategoryNameNotUniqueException.class,
            ParticipantLimitExceedException.class, EventDataConstraintException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictRequest(final Exception e) {
        String reason = "Integrity constraint has been violated";
        return new ApiError(e.getMessage(), reason, HttpStatus.CONFLICT);
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleInternalServerError(final Exception e) {
        String reason = "Error occurred";
        e.printStackTrace();
        log.info(e.getClass().getName());
        return new ApiError(e.getMessage(), reason, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

