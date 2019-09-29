package eu.chessdata.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Hello {

    @GetMapping("/hello/firstHello")
    public String firstHello() {
        return "Hello from Hello controller";
    }
}
