package com.liepin.pojo.ao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserCountsAO {

    private Integer userType;
    private String date;
    private Long counts;
    private String industry;
    private String sex;
    private String year;

}
