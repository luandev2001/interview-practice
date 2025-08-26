package com.xuanluan.practice.optimise.model.parser;

import lombok.Getter;
import lombok.Setter;

//@Getter
//@Setter
public class UserCsvParser {
    private int id;
    private String name;
    private String email;
    private int age;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
}
