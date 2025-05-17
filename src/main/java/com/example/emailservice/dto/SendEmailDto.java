package com.example.emailservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendEmailDto {
    String from;
    String to;
    String subject;
    String body;
}
