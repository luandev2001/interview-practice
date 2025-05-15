package com.xuanluan.practice.interviewnonblocking.test;

import com.xuanluan.practice.interviewnonblocking.InterviewNonBlockingApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = InterviewNonBlockingApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class InterviewNonBlockingApplicationTests {

}
