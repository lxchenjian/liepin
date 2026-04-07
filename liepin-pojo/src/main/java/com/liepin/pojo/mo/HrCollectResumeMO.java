package com.liepin.pojo.mo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * HR收藏的简历
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document("hr_collect_resume")
public class HrCollectResumeMO {

    @Id
    private String id;

    @Field("hr_id")
    private String hrId;

    @Field("resume_expect_id")
    private String resumeExpectId;

    @Field("create_time")
    private LocalDateTime createTime;

}
