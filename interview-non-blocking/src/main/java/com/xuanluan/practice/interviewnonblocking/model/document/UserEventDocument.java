package com.xuanluan.practice.interviewnonblocking.model.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.Instant;

@Setting(settingPath = "elasticsearch/search_settings.json")
@Getter
@Setter
@Document(indexName = "user_event", writeTypeHint = WriteTypeHint.FALSE)
public class UserEventDocument extends BaseDocument {
    @Field(type = FieldType.Keyword)
    private String type;
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Instant createdAt;
}
