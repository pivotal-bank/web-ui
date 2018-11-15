package io.pivotal.web.controller;

import java.security.Principal;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import io.pivotal.web.domain.RegistrationRequest;
import io.pivotal.web.domain.User;
import io.pivotal.web.domain.AuthenticationRequest;
import io.pivotal.web.service.MarketSummaryService;
import io.pivotal.web.service.PortfolioService;
import io.pivotal.web.service.UserService;
import io.pivotal.web.service.AccountService;
import io.pivotal.web.service.QuotesService;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Slf4j
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private QuotesService marketService;
	
	@Autowired
	private PortfolioService portfolioService;
	
	@Autowired
	private MarketSummaryService summaryService;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String showHome(Model model, @AuthenticationPrincipal Authentication authentication) {
		if (!model.containsAttribute("login")) {
			model.addAttribute("login", new AuthenticationRequest());
		}
		model.addAttribute("marketSummary", summaryService.getMarketSummary());
		
		//check if user is logged in!
		if (true) {
		    String currentUserName = "blah";//authentication.getName();
		    log.debug("User logged in: " + currentUserName);
		    
		    try {
		    	model.addAttribute("accounts",accountService.getAccounts(currentUserName));
		    	model.addAttribute("portfolio",portfolioService.getPortfolio(currentUserName));
		    } catch (HttpServerErrorException e) {
		    	model.addAttribute("portfolioRetrievalError",e.getMessage());
		    }

		    if (authentication != null && authentication.isAuthenticated()) {
				User user = userService.getUser(authentication.getName());
				model.addAttribute("user", user);
			}
		    model.addAttribute("accounts",accountService.getAccounts(currentUserName));
		}
		
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
		return "redirect:/?message=" + String.format("User %s successfully registered", registrationRequest.getEmail());
	}
	@ExceptionHandler({ Exception.class })
	public ModelAndView error(HttpServletRequest req, Exception exception) {
		log.debug("Handling error: " + exception);
		log.warn("Exception:", exception);
		ModelAndView model = new ModelAndView();
		model.addObject("errorCode", exception.getMessage());
		model.addObject("errorMessage", exception);
		model.setViewName("error");
		return model;
	}
}
