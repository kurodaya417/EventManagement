package com.eventmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller to serve the frontend UI
 */
@Controller
public class WebController {

    /**
     * Serve the main UI application
     */
    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }
    
    /**
     * Serve the UI for management interface
     */
    @GetMapping("/ui")
    public String ui() {
        return "forward:/index.html";
    }
    
    /**
     * Serve the UI for management interface with path
     */
    @GetMapping("/ui/**")
    public String uiPath() {
        return "forward:/index.html";
    }
}