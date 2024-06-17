package com.example.demo.data.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid leave type")
public class InvalidLeaveTypeException extends RuntimeException
{
}
