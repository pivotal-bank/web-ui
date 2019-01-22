package io.pivotal.web.controller;

import io.pivotal.web.domain.Order;
import io.pivotal.web.service.AccountService;
import io.pivotal.web.service.MarketSummaryService;
import io.pivotal.web.service.PortfolioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpServerErrorException;

@Controller
@PreAuthorize("hasAuthority('ROLE_PORTFOLIO')")
public class PortfolioController {
    private static final Logger logger = LoggerFactory
            .getLogger(PortfolioController.class);

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private MarketSummaryService summaryService;

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/portfolio", method = RequestMethod.GET)
    public String portfolio(Model model, @RegisteredOAuth2AuthorizedClient("pivotalbank") OAuth2AuthorizedClient oAuth2AuthorizedClient) {
        logger.debug("/portfolio");
        model.addAttribute("marketSummary", summaryService.getMarketSummary());
        model.addAttribute("portfolio", portfolioService.getPortfolio(oAuth2AuthorizedClient));
        model.addAttribute("accounts", accountService.getAccounts(oAuth2AuthorizedClient));
        model.addAttribute("order", new Order());
        return "portfolio";
    }

}
