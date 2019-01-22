package io.pivotal.web.service;

import com.newrelic.api.agent.Trace;
import io.pivotal.web.domain.CompanyInfo;
import io.pivotal.web.domain.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;


@Service
@RefreshScope
public class QuotesService {
	private static final Logger logger = LoggerFactory
			.getLogger(QuotesService.class);

	@Autowired
	private WebClient webClient;

    @Value("${pivotal.quotesService.name}")
	private String quotesService;
	
	//@HystrixCommand(fallbackMethod = "getQuoteFallback")
	@Trace(async = true)
	public Quote getQuote(String symbol) {
		logger.debug("Fetching quote: " + symbol);
		List<Quote> quotes = getMultipleQuotes(symbol);
		if (quotes.size() == 1 ) {
			logger.debug("Fetched quote: " + quotes.get(0));
			return quotes.get(0);
		}
		logger.debug("exception: should only be 1 quote and got multiple or zero: " + quotes.size());
		return new Quote();
	}
	
	private Quote getQuoteFallback(String symbol) {
		logger.debug("Fetching fallback quote for: " + symbol);
		Quote quote = new Quote();
		quote.setSymbol(symbol);
		quote.setStatus("FAILED");
		return quote;
	}

	//@HystrixCommand(fallbackMethod = "getCompaniesFallback")
	@Trace(async = true)
	public List<CompanyInfo> getCompanies(String name) {
		logger.debug("Fetching companies with name or symbol matching: " + name);

		ParameterizedTypeReference<List<CompanyInfo>> typeRef = new ParameterizedTypeReference<List<CompanyInfo>>() {};
		List<CompanyInfo> companyInfoList = webClient
				.get()
				.uri("//" + quotesService + "/v1/company/" + name)
				.retrieve()
				.bodyToMono(typeRef)
				.block();
		//CompanyInfo[] infos = restTemplate.getForObject("//" + quotesService + "/v1/company/{name}", CompanyInfo[].class, name);
		return companyInfoList;
	}
	private List<CompanyInfo> getCompaniesFallback(String name) {
		List<CompanyInfo> infos = new ArrayList<>();
		return infos;
	}
	/**
	 * Retrieve multiple quotes.
	 * 
	 * @param symbols comma separated list of symbols.
	 * @return
	 */
	@Trace(async = true)
	public List<Quote> getMultipleQuotes(String symbols) {
		logger.debug("retrieving multiple quotes: " + symbols);
		ParameterizedTypeReference<List<Quote>> typeRef = new ParameterizedTypeReference<List<Quote>>() {};
		List<Quote> quotes = webClient
				.get()
				.uri("//" + quotesService + "/v1/quotes?q=" + symbols)
				.retrieve()
				.bodyToMono(typeRef)
				.block();

		//Quote[] quotesArr = restTemplate.getForObject("//" + quotesService + "/v1/quotes?q={symbols}", Quote[].class, symbols);
		//List<Quote> quotes = Arrays.asList(quotesArr);
		logger.debug("Received quotes: {}",quotes);
		return quotes;
		
	}
	/**
	 * Retrieve multiple quotes.
	 * 
	 * @param symbols
	 * @return
	 */
	public List<Quote> getMultipleQuotes(String[] symbols) {
		logger.debug("Fetching multiple quotes array: {} ",Arrays.asList(symbols));
		
		return getMultipleQuotes(Arrays.asList(symbols));
	}
	/**
	 * Retrieve multiple quotes.
	 * 
	 * @param symbols
	 * @return
	 */
	public List<Quote> getMultipleQuotes(Collection<String> symbols) {
		logger.debug("Fetching multiple quotes array: {} ",symbols);
		StringBuilder builder = new StringBuilder();
		for (Iterator<String> i = symbols.iterator(); i.hasNext();) {
			builder.append(i.next());
			if (i.hasNext()) {
				builder.append(",");
			}
		}
		return getMultipleQuotes(builder.toString());
	}

}
