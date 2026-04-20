package com.huacai.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huacai.system.entity.SysUserModule;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserModuleMapper extends BaseMapper<SysUserModule> {

    @Select("""
            SELECT module_key
            FROM sys_user_module
            WHERE user_id = #{userId}
            ORDER BY id
            """)
    List<String> selectModuleKeysByUserId(Long userId);
}
