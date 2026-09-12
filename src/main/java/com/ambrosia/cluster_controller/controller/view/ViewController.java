package com.ambrosia.cluster_controller.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ViewController {
    @GetMapping("/")
    public String view() {
        return "forward:/index.html";
    }
        
}
