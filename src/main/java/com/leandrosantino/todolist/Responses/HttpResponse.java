package com.leandrosantino.todolist.Responses;

import lombok.Data;

@Data
public class HttpResponse<T> {
    private String message;
    private T data;
}
