package fr.arkyan.popo.pouletmecaniquebackend.controller;

import fr.arkyan.popo.pouletmecaniquebackend.data.entity.FeedProperty;
import fr.arkyan.popo.pouletmecaniquebackend.service.IFeedPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedproperty")
public class FeedPropertyController {

    @Autowired
    private IFeedPropertyService feedPropertyService;

    @GetMapping()
    public List<FeedProperty> get() {
        return feedPropertyService.getAll();
    }

    @PostMapping()
    public FeedProperty save(@RequestBody FeedProperty feedProperty) {
        return feedPropertyService.save(feedProperty);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        feedPropertyService.deleteById(id);
    }

}
