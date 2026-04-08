package com.liepin.pojo.eo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(indexName = "aggs_user")
public class AggsUsersEO {

    @Field
    private Integer type;
    @Field
    private String userId;
    @Field
    private String companyId;
    @Field
    private String resumeId;
    @Field
    private String nickname;
    @Field
    private Integer sex;
    @Field
    private String face;
    @Field
    private String province;
    @Field
    private String industry;
    @Field
    private String companyName;
    @Field
    private String createDate;

}
