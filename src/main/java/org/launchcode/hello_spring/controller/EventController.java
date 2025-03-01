package org.launchcode.hello_spring.controller;

import jakarta.validation.Valid;
import org.launchcode.hello_spring.data.EventCategoryRepository;
import org.launchcode.hello_spring.data.EventRepository;
import org.launchcode.hello_spring.data.TagRepository;
import org.launchcode.hello_spring.models.Event;
import org.launchcode.hello_spring.models.EventCategory;
import org.launchcode.hello_spring.models.Tag;
import org.launchcode.hello_spring.models.dto.EventTagDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventCategoryRepository eventCategoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @GetMapping
    public String displayAllEvents(@RequestParam(required = false) Integer categoryId, Model model) {
        if (categoryId == null) {
            model.addAttribute("events", eventRepository.findAll());
            model.addAttribute("title", "All Events");
        } else {
            Optional<EventCategory> result = eventCategoryRepository.findById(categoryId);
            if (result.isPresent()) {
                EventCategory category = result.get();
                model.addAttribute("title", "Events in Category: " + category.getName());
                model.addAttribute("events", category.getEvents());
            }else {
                model.addAttribute("title", "Invalid Category" + categoryId);
                model.addAttribute("events", eventRepository.findAll());
            }
        }

        return "events/index";
    }

    @GetMapping("create")
    public String renderCreateEventForm(Model model) {
        model.addAttribute("title", "Create Event");
        model.addAttribute(new Event());
        model.addAttribute("categories", eventCategoryRepository.findAll());
        return "events/create";
    }

    @PostMapping("create")
    public String createEvent(@ModelAttribute @Valid Event event,
                              Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("title", "Create Event");
            // model.addAttribute("errorMsg", "Bad data");
            return "events/create";
        }
        eventRepository.save(event);
        return "redirect:/events";
    }

    @GetMapping("delete")
    public String displayDeleteForm(Model model) {
        model.addAttribute("title", "Delete Event");
        model.addAttribute("events", eventRepository.findAll());
        return "events/delete";
    }

    @PostMapping("delete")
    public String deleteEvent(@RequestParam(required = false) int[] eventIds) {
        if (eventIds != null && eventIds.length > 0) {
            for (int eventId : eventIds) {
                eventRepository.deleteById(eventId);
            }
        }
        return "redirect:/events";
    }

    @GetMapping("detail")
    public String displayEventDetails(@RequestParam Integer eventId, Model model) {
        Optional<Event> result = eventRepository.findById(eventId);

        if (result.isEmpty()) {
            model.addAttribute("title", "Invalid Event ID: " + eventId);
        } else {
            Event event = result.get();
            model.addAttribute("title", event.getName() + " Details");
            model.addAttribute("event", event);
            model.addAttribute("tags", event.getTags());
        }

        return "events/detail";
    }

    @GetMapping("add-tag")
    public String displayAddTagForm(@RequestParam Integer eventId, Model model) {
        Optional<Event> result = eventRepository.findById(eventId);
        Event event = result.get();
        model.addAttribute("title", "Add Tag to: " + event.getName());
        model.addAttribute("tags", tagRepository.findAll());
        EventTagDTO eventTag = new EventTagDTO();
        eventTag.setEvent(event);
        model.addAttribute("eventTag", eventTag);

        return "events/add-tag";
    }

    @PostMapping("add-tag")
    public String processAddTagForm(@ModelAttribute @Valid EventTagDTO eventTag, Errors errors, Model model) {
        if (!errors.hasErrors()) {
            Event event = eventTag.getEvent();
            Tag tag = eventTag.getTag();

            if (!event.getTags().contains(tag)) {
                event.addTag(tag);
                eventRepository.save(event);
            }

            return "redirect:detail?eventId=" + event.getId();
        }

        return "redirect:add-tag";
    }
}
