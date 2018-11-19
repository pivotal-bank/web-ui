package io.pivotal.web.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import io.pivotal.web.domain.Account;

import io.pivotal.web.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@Service
@RefreshScope
public class AccountService {
    private static final Logger logger = LoggerFactory
            .getLogger(AccountService.class);

    @Autowired
    private WebClient webClient;

    @Value("${pivotal.accountsService.name}")
    private String accountsService;

    public void createAccount(Account account, OAuth2AuthorizedClient oAuth2AuthorizedClient ) {
        logger.debug("Creating account ");
        String status = webClient
                .post()
                .uri("//" + accountsService + "/accounts/")
                .attributes(oauth2AuthorizedClient(oAuth2AuthorizedClient))
                .syncBody(account)
                .retrieve()
                .bodyToMono(String.class)
                .block();
       // String status = oAuth2RestTemplate.postForObject("//" + accountsService + "/accounts/", account, String.class);
        logger.info("Status from registering account is " + status);
    }



    public List<Account> getAccounts(OAuth2AuthorizedClient oAuth2AuthorizedClient) {
        logger.debug("Looking for accounts");
        ParameterizedTypeReference<List<Account>> typeRef = new ParameterizedTypeReference<List<Account>>() {};
        List accounts = webClient
                .get()
                .uri("//" + accountsService + "/accounts")
                .attributes(oauth2AuthorizedClient(oAuth2AuthorizedClient))
                .retrieve()
                .bodyToMono(typeRef)
                .block();

       // Account[] accounts = oAuth2RestTemplate.getForObject("//" + accountsService + "/accounts", Account[].class);
        return accounts;
    }

    public List<Account> getAccountsFallback() {
        logger.warn("Invoking fallback for getAccount");
        return new ArrayList<>();
    }

    public List<Account> getAccountsByType(String type, OAuth2AuthorizedClient oAuth2AuthorizedClient) {
        logger.debug("Looking for account with type: " + type);
        ParameterizedTypeReference<List<Account>> typeRef = new ParameterizedTypeReference<List<Account>>() {};
        List accounts = webClient
                .get()
                .uri("//" + accountsService + "/accounts?type=" + type)
                .attributes(oauth2AuthorizedClient(oAuth2AuthorizedClient))
                .retrieve()
                .bodyToMono(typeRef)
                .block();
       // Account[] accounts = oAuth2RestTemplate.getForObject("//" + accountsService + "/accounts?type={type}", Account[].class, type);
        return accounts;
    }

}
