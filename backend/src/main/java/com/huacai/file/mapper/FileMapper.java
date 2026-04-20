package com.huacai.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huacai.file.entity.SysFile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper extends BaseMapper<SysFile> {
}
