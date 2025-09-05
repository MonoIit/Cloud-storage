package com.example.cloud_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;

@JsonTypeName("_file_put_request")
@AllArgsConstructor
@Data
public class FilePutRequest {

    @JsonProperty("name")
    private String name;
}
