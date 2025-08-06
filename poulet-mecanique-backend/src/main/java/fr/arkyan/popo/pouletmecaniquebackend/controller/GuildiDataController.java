package fr.arkyan.popo.pouletmecaniquebackend.controller;

import fr.arkyan.popo.pouletmecaniquebackend.service.IGuildiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/guildi")
public class GuildiDataController {

    @Autowired
    private IGuildiService guildiService;

    @GetMapping("/categories")
    public List<String> getGuildiCategories() {
        return guildiService.getGuildiCategories();
    }

}
