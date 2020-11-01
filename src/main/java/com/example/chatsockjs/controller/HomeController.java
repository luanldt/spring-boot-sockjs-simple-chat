package com.example.chatsockjs.controller;

import java.util.Base64;
import java.util.UUID;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

	public static final String APP_ID = "WSDemo";

	@GetMapping("")
	public ModelAndView home() {
		ModelAndView mv = new ModelAndView("index");
		String appToken = new String(Base64.getEncoder().encode((APP_ID + UUID.randomUUID().toString()).getBytes()));
		mv.addObject("appToken", appToken);
		return mv;
	}

}
