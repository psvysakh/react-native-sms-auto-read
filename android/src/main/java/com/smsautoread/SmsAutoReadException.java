package com.smsautoread;

public class SmsAutoReadException extends RuntimeException{
  public String code;

  SmsAutoReadException(Errors error, String message) {
    super(message);
    this.code = error.toString();
  }
}
