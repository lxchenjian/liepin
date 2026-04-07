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
 * 谁看过我 - HR查看用户简历
 * 显示的内容不多，并不复杂，所以没有必要去查数据库
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document("who_look_me")
public class WhoLookMeMO {

    @Id
    private String id;

    @Field("hr_id")
    private String hrId;

    @Field("hr_company_id")
    private String hrCompanyId;

    @Field("hr_face")
    private String hrFace;

    @Field("hr_nickname")
    private String hrNickname;

    @Field("hr_company_name")
    private String hrCompanyName;

    @Field("hr_position")
    private String hrPosition;

    @Field("cand_user_id")
    private String candUserId;

    @Field("create_time")
    private LocalDateTime createTime;

}
