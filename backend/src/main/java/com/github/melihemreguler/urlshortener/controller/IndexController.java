package com.github.melihemreguler.urlshortener.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@Slf4j
public class IndexController {

    @GetMapping("/")
    public RedirectView redirectToSwagger() {
        log.info("Redirecting to Swagger UI");
        return new RedirectView("swagger-ui.html");
    }
}
