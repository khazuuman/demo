package com.example.demo.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class Errors {

    String message;
    Map<String, String> errors;

}
