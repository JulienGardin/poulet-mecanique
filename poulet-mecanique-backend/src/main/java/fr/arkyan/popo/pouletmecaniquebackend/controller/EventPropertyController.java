package fr.arkyan.popo.pouletmecaniquebackend.controller;

import fr.arkyan.popo.pouletmecaniquebackend.data.entity.EventProperty;
import fr.arkyan.popo.pouletmecaniquebackend.service.IEventPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eventproperty")
public class EventPropertyController {

    @Autowired
    private IEventPropertyService eventPropertyService;

    @GetMapping()
    public List<EventProperty> get() {
        return eventPropertyService.getAll();
    }

    @PostMapping()
    public EventProperty save(@RequestBody EventProperty eventProperty) {
        return eventPropertyService.save(eventProperty);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        eventPropertyService.deleteById(id);
    }



}
