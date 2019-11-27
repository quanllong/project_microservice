package com.stylefeng.guns.rest.service;

import com.stylefeng.guns.rest.service.vo.UserVo;

public interface UserService {
    // 现在还没有user表，暂时用mtime_actor_t
    UserVo selectById(Integer id);
}
