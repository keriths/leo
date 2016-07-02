package com.fs.leo.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by fanshuai on 16/7/2.
 */
@Service("testService")
public class TestService {

    @Value("${name}")
    private String name;

    public String getName(){
        return name;
    }
}
