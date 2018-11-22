package io.pivotal.web.service;

import io.pivotal.web.domain.Order;
import io.pivotal.web.domain.Portfolio;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.hystrix.HystrixCommands;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

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
				.contentType(MediaType.APPLICATION_JSON)
				.syncBody(order)
				.attributes(oauth2AuthorizedClient(oAuth2AuthorizedClient))
				.retrieve()
				.bodyToMono(Order.class)
				.block();

		/**
		ResponseEntity<Order>  result = restTemplate.postForEntity("//" + portfolioService + "/portfolio", order, Order.class);
		if (result.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			throw new OrderNotSavedException("Could not save the order");
		}**/
		logger.debug("Order saved:: " + savedOrder);
		return order;
	}
	
	//@HystrixCommand(fallbackMethod = "getPortfolioFallback",  commandProperties = {@HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")} )
	public Portfolio getPortfolio(OAuth2AuthorizedClient oAuth2AuthorizedClient ) {

		Publisher<Portfolio> portfolioPublisher = webClient
				.get()
				.uri("//" + portfolioService + "/portfolio")
				.attributes(oauth2AuthorizedClient(oAuth2AuthorizedClient))
				.retrieve()
				.bodyToMono(Portfolio.class);

		Portfolio portfolio = HystrixCommands
				.from( portfolioPublisher )
				.eager()
				.commandName("portfolio")
				.fallback(Flux.just(getPortfolioFallback()))
				.toMono()
				.block();

		//Portfolio folio = restTemplate.getForObject("//" + portfolioService + "/portfolio", Portfolio.class, user);
		logger.debug("Portfolio received: " + portfolio);
		return portfolio;
	}
	
	private Portfolio getPortfolioFallback() {
		logger.debug("Portfolio fallback");
		Portfolio folio = new Portfolio();
		//folio.setAccountId(accountId);
		return folio;
	}

}
