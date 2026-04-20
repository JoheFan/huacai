package com.huacai.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huacai.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<SysUser> {
}
