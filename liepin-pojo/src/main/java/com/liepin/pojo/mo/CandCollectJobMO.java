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
 * 求职者收藏职位
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document("cand_collect_job")
public class CandCollectJobMO {

    @Id
    private String id;

    @Field("cand_user_id")
    private String candUserId;

    @Field("job_id")
    private String jobId;

    @Field("create_time")
    private LocalDateTime createTime;

}
