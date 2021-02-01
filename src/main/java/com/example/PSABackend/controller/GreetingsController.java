package com.example.PSABackend.controller;

import com.example.PSABackend.classes.Greeting;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingsController {

    private final String thing = "Bye Bye";
    private final AtomicLong counter = new AtomicLong();

    @PostMapping(path = "/greeting", consumes = "application/json", produces = "application/json")
    public Greeting sayHi(@RequestBody Greeting greet){

        return new Greeting(counter.incrementAndGet(), greet.getContent());
    }

//    private static final String template = "BYEBYEEEE, %s!";
//    private final AtomicLong counter = new AtomicLong();
//    @GetMapping("/greeting")
//    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
//        return new Greeting(counter.incrementAndGet(), String.format(template, name), "COCK");
//    }
}
