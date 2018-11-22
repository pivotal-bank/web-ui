package io.pivotal.web.controller;

import io.pivotal.web.domain.RegistrationRequest;
import io.pivotal.web.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@Slf4j
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private PortfolioService portfolioService;
	
	@Autowired
	private MarketSummaryService summaryService;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String showHome(Model model,Principal princ,  @AuthenticationPrincipal OAuth2User principal){
		//check if user is logged in!
		if (principal != null) {
			return "redirect:/home";
		}
		model.addAttribute("marketSummary", summaryService.getMarketSummary());
		return "index";
	}

	@GetMapping("/home")
	public String authorizedHome(Model model, @AuthenticationPrincipal OAuth2User principal, @RegisteredOAuth2AuthorizedClient("pivotalbank") OAuth2AuthorizedClient oAuth2AuthorizedClient) {
		model.addAttribute("marketSummary", summaryService.getMarketSummary());
		String currentUserName = principal.getName();
		log.debug("User logged in: " + currentUserName);
		model.addAttribute("accounts",accountService.getAccounts(oAuth2AuthorizedClient));
		model.addAttribute("portfolio",portfolioService.getPortfolio(oAuth2AuthorizedClient));
		model.addAttribute("user", userService.getUser(currentUserName, oAuth2AuthorizedClient, principal));
		return "index";
	}


	@RequestMapping(value = "/registration", method = RequestMethod.GET)
	public String registration(Model model) {
		model.addAttribute("registration", new RegistrationRequest());
		return "registration";
	}

	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public String register(Model model, @Valid @ModelAttribute(value="registration") RegistrationRequest registrationRequest,
						   BindingResult bindingResult) {
		log.info("register: user:" + registrationRequest.getEmail());
		if (bindingResult.hasErrors()) {
			model.addAttribute("errors", bindingResult);
			return "registration";
		}
		this.userService.registerUser(registrationRequest);
		return FlashService.redirectWithMessage( "/", String.format("User %s successfully registered", registrationRequest.getEmail()));
	}
}
