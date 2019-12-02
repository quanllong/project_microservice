package com.stylefeng.guns.rest.service.vo.filmvo;

import lombok.Data;

import java.util.List;

/**
 * @Author:ys
 * @Date:2019/11/29
 * @time:15:54
 */
@Data
public class FilmConditionVO {

    private List<CatInfoVO> catInfo;

    private List<SourceInfoVO> sourceInfo;

    private List<YearInfoVO> yearInfo;
}
