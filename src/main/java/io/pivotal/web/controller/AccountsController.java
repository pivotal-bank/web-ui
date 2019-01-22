package io.pivotal.web.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import io.pivotal.web.domain.Account;
import io.pivotal.web.domain.Order;
import io.pivotal.web.domain.Search;
import io.pivotal.web.service.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.servlet.ModelAndView;

@Controller
@PreAuthorize("hasAuthority('ROLE_ACCOUNT')")
public class AccountsController {
	private static final Logger logger = LoggerFactory
			.getLogger(AccountsController.class);
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private MarketSummaryService summaryService;
	
	@RequestMapping(value = "/accounts", method = RequestMethod.GET)
	public String accounts(Model model, @RegisteredOAuth2AuthorizedClient("pivotalbank") OAuth2AuthorizedClient oAuth2AuthorizedClient) {
		logger.debug("/accounts");
		model.addAttribute("marketSummary", summaryService.getMarketSummary());
		model.addAttribute("accounts",accountService.getAccounts(oAuth2AuthorizedClient));
		return "accounts";
	}
	
	@RequestMapping(value = "/openaccount", method = RequestMethod.GET)
	public String openAccount(Model model) {
		Account account = new Account();
		account.setOpenbalance(new BigDecimal(100000));
		model.addAttribute("newaccount",account);
		return "openaccount";
	}
	
	@RequestMapping(value = "/openaccount", method = RequestMethod.POST)
	public String saveAccount(Model model,@ModelAttribute(value="newaccount") Account account,  @RegisteredOAuth2AuthorizedClient("pivotalbank") OAuth2AuthorizedClient oAuth2AuthorizedClient) {
		logger.debug("saveAccounts: creating account: " + account);
		account.setBalance(account.getOpenbalance());
		account.setCreationdate(new Date());
		
		logger.info("saveAccounts: saving account: " + account);
		
		accountService.createAccount(account, oAuth2AuthorizedClient);

		return FlashService.redirectWithMessage("/accounts", String.format("Account '%s' created successfully", account.getName()));
	}

}
