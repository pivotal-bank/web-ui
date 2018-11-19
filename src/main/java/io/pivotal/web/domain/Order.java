package io.pivotal.web.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Order {

	private Integer orderId;
	
	private Integer accountId;

	private String symbol;

	private BigDecimal orderFee;

	private Date completionDate;

	private OrderType orderType;

	private BigDecimal price;

	private Integer quantity;
	
	private String currency;



}
