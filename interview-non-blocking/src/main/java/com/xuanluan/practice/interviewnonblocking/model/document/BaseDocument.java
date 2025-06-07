package com.xuanluan.practice.interviewnonblocking.model.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
public class BaseDocument {
    @Id
    @Field(type = FieldType.Keyword)
    private String id;
}
