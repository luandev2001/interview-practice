package com.xuanluan.practice.interviewnonblocking.service;

public interface IWriteService<T, CreateDTO, UpdateDTO> extends ICreateService<T, CreateDTO>, IUpdateService<T, UpdateDTO> {
}
