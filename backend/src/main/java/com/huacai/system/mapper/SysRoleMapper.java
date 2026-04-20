package com.huacai.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huacai.system.entity.SysRole;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    @Select("""
            SELECT r.role_code
            FROM sys_role r
            INNER JOIN sys_user_role ur ON ur.role_id = r.id
            WHERE ur.user_id = #{userId}
              AND r.status = 'ENABLE'
            ORDER BY r.id
            """)
    List<String> selectRoleCodesByUserId(Long userId);

    @Select("""
            SELECT r.*
            FROM sys_role r
            INNER JOIN sys_user_role ur ON ur.role_id = r.id
            WHERE ur.user_id = #{userId}
              AND r.status = 'ENABLE'
            ORDER BY r.id
            """)
    List<SysRole> selectEnabledRolesByUserId(Long userId);
}
