package com.liepin.test;

import com.liepin.retry.RetryComponent;
import com.liepin.task.SMSTask;
import com.liepin.retry.RetryComponent;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class SpringRetryTest {

    @Autowired
    private RetryComponent retryComponent;

    @Autowired
    private SMSTask smsTask;

    @Test
    public void retry() {
        boolean res = retryComponent.sendSmsWithRetry();
        log.info("最终运行结果为 res = {}", res);
    }

    @Test
    public void sendWithTask() {
        smsTask.sendSMSTask();
        log.info("sendWithTask 调用结束...");
    }

}
