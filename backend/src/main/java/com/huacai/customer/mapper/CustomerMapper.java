package com.huacai.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huacai.customer.entity.CustCustomer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper extends BaseMapper<CustCustomer> {
}
