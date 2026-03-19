package com.liepin.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAdminBO {
    private String id;
    private String username;
    private String face;
    private String remark;
}
