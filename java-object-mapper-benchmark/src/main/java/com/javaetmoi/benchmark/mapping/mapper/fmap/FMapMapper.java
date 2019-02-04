package com.javaetmoi.benchmark.mapping.mapper.fmap;

import com.javaetmoi.benchmark.mapping.mapper.OrderMapper;
import com.javaetmoi.benchmark.mapping.model.dto.OrderDTO;
import com.javaetmoi.benchmark.mapping.model.entity.Order;
import sk.annotation.library.mapper.fast.utils.context.MapperUtil;

public class FMapMapper implements OrderMapper {

	private FMapOrderMapper mapper = MapperUtil.getMapper(FMapOrderMapper.class);

	@Override
	public OrderDTO map(Order source) {
		return mapper.map(source);
	}

	public static void main(String[] args) {
		MapperUtil.getMapper(FMapOrderMapper.class);
	}
}
