package io.pivotal.web.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Account {

	@JsonProperty("id")
	private Integer id;

	@JsonProperty("name")
	private String name;
	
	@JsonProperty("creationdate")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss")
    private Date creationdate;
	
	@JsonProperty("openbalance")
    private BigDecimal openbalance;

	@JsonProperty("balance")
    private BigDecimal balance;

	@JsonProperty("type")
	private String type;

	@JsonProperty("currency")
	private String currency;

 }
