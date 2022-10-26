package com.example.mockgenerator.model;

import lombok.Data;

import java.util.List;

@Data
public class MockRequest {
    private String fileType;
    private MockType mockType;
    private List<String> fields;
    private int limit;
}
