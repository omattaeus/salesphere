package com.salesphere.salesphere.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class ViewController {

    @GetMapping("/sale")
    public String showSalePage() {
        return "sale";
    }
}