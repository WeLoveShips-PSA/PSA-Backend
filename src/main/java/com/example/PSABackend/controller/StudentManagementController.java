package com.example.PSABackend.controller;


import com.example.PSABackend.DAO.AlertDAO;
import com.example.PSABackend.PortNetConnector;
import com.example.PSABackend.classes.Student;
import com.example.PSABackend.exceptions.PSAException;
import com.example.PSABackend.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RequestMapping(path = "students")
@RestController
public class StudentManagementController {
    @Autowired
    PortNetConnector portNetConnector = new PortNetConnector();
    AlertService alertService = new AlertService();

    private final List<Student> STUDENTS = Arrays.asList(
            new Student(1, "James Bond"),
            new Student(2, "Maria Jones"),
            new Student(3, "Anna Smith")
    );

    @GetMapping(path = "all")
    public List<Student> getSTUDENTS() {
        // portNetConnector.getUpdate("2021-04-11", "2021-04-17");
        try {
            portNetConnector.updateVessel();
            alertService.getAlerts();
        } catch (Exception e) {
            System.out.println("caight in smc");
            e.printStackTrace();
        }

        return STUDENTS;
    }

    @PostMapping(path = "add")
    public List<Student> registerNewStudent(@RequestBody Student student) {
        STUDENTS.add(student);
        return STUDENTS;
    }

    @PostMapping
    @RequestMapping("delete")
    public void deleteStudent(@RequestBody Student student) {
        System.out.println(student.getStudentName());
    }

    @PostMapping
    @RequestMapping("/updateStudent")
    public void updateStudent(@RequestBody Student student){
        System.out.println("Hello");
    }
}