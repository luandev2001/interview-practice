package com.xuanluan.practice.paygate.model.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Bank extends BaseEntity {
    private String name;
    private String code;
    private String bin;
    private String shortName;
    private String swiftCode;
    private String logo;
    private boolean isDeleted;
}
