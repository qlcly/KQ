package com.example.demo.data.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Please check the param")
public class BadRequestException extends RuntimeException {
    public BadRequestException(){ }
    public static void checkHasNull(Object... objects) throws BadRequestException{
        for(Object object: objects){
            if(object == null){
                throw new BadRequestException();
            }
        }

    }
    public static void checkAllNull(Object... objects) throws BadRequestException {
        boolean allNull = true;
        for(Object object: objects){
            if(object != null){
                allNull = false;
                break;
            }
        }
        if(allNull){
            throw new BadRequestException();
        }
    }
}
