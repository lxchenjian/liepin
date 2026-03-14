package com.liepin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liepin.mapper.UsersMapper;
import com.liepin.pojo.Users;
import com.liepin.service.UsersService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author 风间影月
 * @since 2026-03-14
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {

}
