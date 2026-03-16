package ru.kata.spring.boot_security.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

	private final UserService userService;
	private final RoleService roleService;

	@GetMapping
	public String listUsers(Model model, @AuthenticationPrincipal User currentUser) {
		model.addAttribute("users", userService.getAllUsers());
		return "admin-list";
	}

	@GetMapping("/add")
	public String showAddForm(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("allRoles", roleService.getAllRoles());
		return "user-form";
	}

	@PostMapping("/add")
	public String addUser(@ModelAttribute("user") @Valid User user,
	                      BindingResult bindingResult,
	                      @RequestParam(value = "roleIds", required = false) List<Long> roleIds,
	                      Model model) {

		if (bindingResult.hasErrors()) {
			model.addAttribute("allRoles", roleService.getAllRoles());

			return "user-form";
		}

		userService.addUser(user, roleIds);
		return "redirect:/admin";
	}

	@GetMapping("/edit")
	public String showEditForm(@RequestParam("id") Long id, Model model) {
		User user = userService.getUserById(id);

		model.addAttribute("user", user);
		model.addAttribute("allRoles", roleService.getAllRoles());
		return "user-form";
	}


	@PostMapping("/update")
	public String updateUser(@ModelAttribute("user") @Valid User user,
	                         BindingResult bindingResult,
	                         @RequestParam(value = "roleIds", required = false) List<Long> roleIds,
	                         Model model) {

		if (bindingResult.hasErrors()) {
			model.addAttribute("allRoles", roleService.getAllRoles());

			return "user-form";
		}

		userService.updateUser(user, roleIds);
		return "redirect:/admin";
	}

	@PostMapping("/delete")
	public String deleteUser(@RequestParam("id") Long id) {
		userService.removeUser(id);
		return "redirect:/admin";
	}
}

