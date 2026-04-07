package com.liepin.pojo.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.liepin.utils.LocalDateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 用户发文的BO
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NewArticleBO {

    private String articleId;

    private String title;
    private String content;
    private String articleCover;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = LocalDateUtils.DATETIME_PATTERN)
    private LocalDateTime publishTime;
}