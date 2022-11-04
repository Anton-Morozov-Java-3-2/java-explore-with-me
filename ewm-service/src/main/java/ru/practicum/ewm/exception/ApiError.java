package ru.practicum.ewm.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Value
public class ApiError {

        List<String> errors = new ArrayList<>();
        String message;
        String reason;
        HttpStatus status;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
        LocalDateTime timestamp;

        public ApiError(final String message, final String reason, HttpStatus status) {
                this.message = message;
                this.reason = reason;
                this.status = status;
                timestamp = LocalDateTime.now();
        }
}
