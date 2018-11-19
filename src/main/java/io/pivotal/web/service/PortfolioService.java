package io.pivotal.web.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import io.pivotal.web.domain.Order;
import io.pivotal.web.domain.Portfolio;
import io.pivotal.web.exception.OrderNotSavedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;


@Service
@RefreshScope
public class PortfolioService {
	private static final Logger logger = LoggerFactory
			.getLogger(PortfolioService.class);

	@Autowired
	private WebClient webClient;

    @Value("${pivotal.portfolioService.name}")
	private String portfolioService;

	public Order sendOrder(Order order, OAuth2AuthorizedClient oAuth2AuthorizedClient ) {
		logger.debug("send order: " + order);
		
		//check result of http request to ensure its ok.
		Order savedOrder = webClient
				.post()
				.uri("//" + portfolioService + "/portfolio")
				.attributes(oauth2AuthorizedClient(oAuth2AuthorizedClient))
				.syncBody(order)
				.retrieve()
				.bodyToMono(Order.class)
				.block();
		/**
		ResponseEntity<Order>  result = restTemplate.postForEntity("//" + portfolioService + "/portfolio", order, Order.class);
		if (result.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			throw new OrderNotSavedException("Could not save the order");
		}**/
		logger.debug("Order saved:: " + savedOrder);
		return savedOrder;
	}
	
	//@HystrixCommand(fallbackMethod = "getPortfolioFallback",  commandProperties = {@HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")} )
	public Portfolio getPortfolio(OAuth2AuthorizedClient oAuth2AuthorizedClient ) {
		Portfolio portfolio = webClient
				.get()
				.uri("//" + portfolioService + "/portfolio")
				.attributes(oauth2AuthorizedClient(oAuth2AuthorizedClient))
				.retrieve()
				.bodyToMono(Portfolio.class)
				.block();

		//Portfolio folio = restTemplate.getForObject("//" + portfolioService + "/portfolio", Portfolio.class, user);
		logger.debug("Portfolio received: " + portfolio);
		return portfolio;
	}
	
	private Portfolio getPortfolioFallback(String accountId) {
		logger.debug("Portfolio fallback");
		Portfolio folio = new Portfolio();
		folio.setAccountId(accountId);
		return folio;
	}

}
