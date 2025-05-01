package com.xuanluan.practice.interviewnonblocking.repository;

import com.xuanluan.practice.interviewnonblocking.model.entity.UserEvent;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface IUserEventRepository extends ReactiveCrudRepository<UserEvent, UUID> {

}
