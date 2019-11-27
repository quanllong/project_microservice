package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.service.FilmService;
import com.stylefeng.guns.rest.service.vo.FilmVO;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FilmController {

    @Reference(interfaceClass = FilmService.class,check = false)
    private FilmService filmService;

    @RequestMapping("film/query")
    public FilmVO query(Integer id){
        FilmVO filmVO = filmService.selectById(id);
        return filmVO;
    }
}
