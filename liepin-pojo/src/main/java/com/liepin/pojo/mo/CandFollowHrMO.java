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
 * 候选人关注HR
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document("cand_follow_hr")
public class CandFollowHrMO {

    @Id
    private String id;

    @Field("cand_user_id")
    private String candUserId;

    @Field("hr_id")
    private String hrId;

    @Field("hr_face")
    private String hrFace;

    @Field("hr_nickname")
    private String hrNickname;

    @Field("hr_company_name")
    private String hrCompanyName;

    @Field("hr_position")
    private String hrPosition;

    @Field("create_time")
    private LocalDateTime createTime;

}
