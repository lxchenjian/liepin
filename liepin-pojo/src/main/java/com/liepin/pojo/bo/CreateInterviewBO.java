package com.liepin.pojo.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.liepin.utils.LocalDateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreateInterviewBO {

    private String id;
    private String hrUserId;
    private String companyId;
    private String candUserId;
    private String jobId;
    private String jobName;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = LocalDateUtils.DATETIME_PATTERN)
    private LocalDateTime interviewTime;
    private String interviewAddress;
    private String remark;
    /**
     * 面试邀约的状态 InterviewStatusEnum.java
     *      1：等待候选人接受面试
     *      2：候选人已接受面试
     *      3：候选人已拒绝面试
     *      4：HR已取消面试
     *      5：面试通过（未使用）
     */
    private Integer status;

    private String candName;
    private String candFace;
    private String candPosition;

}
