package com.raccoon.qqbot.exception;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class ReturnedException extends RuntimeException {
    public int code;
    public String message;

    public ReturnedException(BindingResult result) {
        this.code = ServiceError.COMMON_INPUT_INVALID.getCode();

        if (!result.hasErrors()) {
            this.message = ServiceError.COMMON_INPUT_INVALID.getMessage();
        } else {
            ObjectError objectError = result.getAllErrors().get(0);
            String message = objectError.getDefaultMessage();
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                if (message.contains("不能为null") || message.contains("may not be null")) {
                    message = fieldError.getField() + " : " + message;
                }

                String macro = "${IDX}";
                if (message.contains(macro)) {
                    String idxStr = fieldError.getField().replaceFirst("^.*\\[([0-9]+)\\].*$", "$1");

                    try {
                        int idx = Integer.parseInt(idxStr) + 1;
                        message = message.replace(macro, idx + "");
                    } catch (NumberFormatException var8) {

                    }
                }
            }

            this.message = message;
        }
    }

    public ReturnedException() {
    }

    public ReturnedException(ServiceError error) {
        super(error.getMessage());
        this.code = error.getCode();
        this.message = error.getMessage();
    }

    public ReturnedException(ServiceError error, String message) {
        super(error.getMessage());
        this.code = error.getCode();
        this.message = message;
    }

    public ReturnedException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
