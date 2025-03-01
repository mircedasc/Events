package org.launchcode.hello_spring.controller;

import org.launchcode.hello_spring.data.EventCategoryRepository;
import org.launchcode.hello_spring.models.EventCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequestMapping("eventCategory")
public class EventCategoryController {

    @Autowired
    private EventCategoryRepository eventCategoryRepository;

    // Display all categories
    @GetMapping
    public String displayAllCategories(Model model) {
        model.addAttribute("title", "All Categories");
        model.addAttribute("categories", eventCategoryRepository.findAll());
        return "eventCategory/index";
    }

    // Render form to create a new category
    @GetMapping("create")
    public String renderCreateCategoryForm(Model model) {
        model.addAttribute("title", "Create Categories");
        model.addAttribute("eventCategory", new EventCategory());
        return "eventCategory/create";
    }

    // Process form submission for creating a new category
    @PostMapping("create")
    public String createCategory(@ModelAttribute @Valid EventCategory newCategory, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("title", "Create Categories");
            return "eventCategory/create";
        }

        eventCategoryRepository.save(newCategory);
        return "redirect:/eventCategory";
    }
}