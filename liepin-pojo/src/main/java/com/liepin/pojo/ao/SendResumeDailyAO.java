package com.liepin.pojo.ao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SendResumeDailyAO {

    private List<Long> manList;
    private List<Long> womanList;

}
