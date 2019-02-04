package com.javaetmoi.benchmark.mapping.mapper.orika;

import com.javaetmoi.benchmark.mapping.mapper.OrderMapper;
import com.javaetmoi.benchmark.mapping.model.dto.OrderDTO;
import com.javaetmoi.benchmark.mapping.model.entity.Order;
import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * Using custom BoundMapperFacade with no object graph cycles.
 *
 * @see <d href="http://orika-mapper.github.io/orika-docs/performance-tuning.html">http://orika-mapper.github.io/orika-docs/performance-tuning.html</d>
 */
public class OrikaMapper implements OrderMapper {

    private BoundMapperFacade<Order, OrderDTO> orderMapper;

    public OrikaMapper() {
        MapperFactory factory = new DefaultMapperFactory.Builder().build();
        factory.registerClassMap(factory.classMap(Order.class, OrderDTO.class)
                .field("customer.name", "customerName")
                .field("customer.billingAddress.street",
                        "billingStreetAddress")
                .field("customer.billingAddress.city", "billingCity")
                .field("customer.shippingAddress.street",
                        "shippingStreetAddress")
                .field("customer.shippingAddress.city",
                        "shippingCity")
                .field("products", "products")
                .toClassMap());
        orderMapper = factory.getMapperFacade(Order.class, OrderDTO.class, false);
    }

    @Override
    public OrderDTO map(Order source) {
        return orderMapper.map(source);
    }
};

