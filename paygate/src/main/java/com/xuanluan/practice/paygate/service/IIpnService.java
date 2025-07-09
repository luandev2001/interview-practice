package com.xuanluan.practice.paygate.service;

public interface IIpnService<T, R> {
    R ipn(T request);
}
