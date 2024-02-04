package com.skilldistillery.artgallery.controllers;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.skilldistillery.artgallery.data.UserDAO;
import com.skilldistillery.artgallery.entities.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
	@Autowired
	private UserDAO userDAO;

	@RequestMapping(path = { "/", "/home" })
	public ModelAndView home() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("home");
		return mv;
	}

	@RequestMapping(path = "/login", method = RequestMethod.GET)
	public String login(HttpSession session) {
		LocalDateTime loginTime = (LocalDateTime) session.getAttribute("loginTime");
		if (loginTime != null) {
			LocalDateTime now = LocalDateTime.now();
			Duration totalTime = Duration.between(loginTime, now);
			session.setAttribute("timeOnSite", totalTime);
			return "home";
		} else {
			return "login";
		}
	}

	@RequestMapping(path = "/login", method = RequestMethod.POST)
	public String doLogin(User user, HttpSession session) {
		try {
			User authenticatedUser = userDAO.authenticateUser(user.getUsername(), user.getPassword());

			if (authenticatedUser != null) {
				session.setAttribute("loggedInUser", authenticatedUser);
				LocalDateTime lt = LocalDateTime.now();
				session.setAttribute("loginTime", lt);
				return "account";
			} else {
				System.out.println("Authentication failed. Redirecting to login.");
				return "login";
			}
		} catch (Exception e) {
			System.out.println("An error occurred during login.");
			e.printStackTrace();
			return "error";
		}
	}

	@RequestMapping("/logout")
	public ModelAndView logOut(User user, HttpSession session) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("home");
		session.removeAttribute("loggedInUser");
		session.removeAttribute("loginTime");
		session.removeAttribute("timeOnSite");
		return mv;
	}

	@RequestMapping(path = "/account", method = RequestMethod.GET)
	public String accountView(HttpSession session) {
		LocalDateTime loginTime = (LocalDateTime) session.getAttribute("loginTime");
		if (loginTime != null) {
			LocalDateTime now = LocalDateTime.now();
			Duration totalTime = Duration.between(loginTime, now);
			session.setAttribute("timeOnSite", totalTime);
			return "account";
		} else {
			return "login";
		}
	}

}
