package com.xuanluan.practice.interviewnonblocking.model.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.*;

@Setting(settingPath = "elasticsearch/search_settings.json")
@Getter
@Setter
@Document(indexName = "product", writeTypeHint = WriteTypeHint.FALSE)
public class ProductDocument extends BaseDocument {
    @Field(type = FieldType.Text, analyzer = "search_no_diacritics")
    private String name;
}
