package com.javaetmoi.benchmark.mapping.mapper.fmap;

import com.javaetmoi.benchmark.mapping.model.dto.OrderDTO;
import com.javaetmoi.benchmark.mapping.model.dto.ProductDTO;
import com.javaetmoi.benchmark.mapping.model.entity.Order;
import com.javaetmoi.benchmark.mapping.model.entity.Product;
import org.mapstruct.MapperConfig;
import sk.annotation.library.mapper.fast.annotations.FastMapper;
import sk.annotation.library.mapper.fast.annotations.FieldMapping;
import sk.annotation.library.mapper.fast.annotations.MapperFieldConfig;

@FastMapper
@MapperFieldConfig(
		fieldMapping = {
				@FieldMapping(s="customer.name", d="customerName"),
				@FieldMapping(s="customer.billingAddress.street", d="billingStreetAddress"),
				@FieldMapping(s="customer.billingAddress.city", d="billingCity"),
				@FieldMapping(s="customer.shippingAddress.street", d="shippingStreetAddress"),
				@FieldMapping(s="customer.shippingAddress.city", d="shippingCity"),
		}
)
public abstract class FMapOrderMapper {

	abstract public OrderDTO map(Order source);

	abstract public ProductDTO map(Product product);
}
