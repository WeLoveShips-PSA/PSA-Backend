package com.example.PSABackend.controller;

import com.example.PSABackend.classes.Company;
import com.example.PSABackend.classes.User;
import com.example.PSABackend.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RequestMapping(path = "company")
@RestController // This means that this class is a Controller

public class CompanyController {

    private final CompanyService companyService;

    @Autowired
    public CompanyController(CompanyService companyService) { this.companyService = companyService; }

    @PostMapping
    @RequestMapping(path = "/add")
    public void addCompany(@Valid @NonNull @RequestBody Company company) {
        companyService.addCompany(company);
        System.out.println(companyService.getAllCompanies().size());
    }

    @GetMapping
    @RequestMapping(path = "/getall")
    public List<Company> getAllCompanies() {
        return companyService.getAllCompanies();
    }

    @GetMapping
    @RequestMapping(path = "/get/{companyId}")
    public Company getCompanyById(@PathVariable("companyId") UUID companyId) {
        return companyService.getCompanyById(companyId)
                .orElse(null);
    }

    @DeleteMapping
    @RequestMapping(path = "/del/{companyId}")
    public void deleteCompanyByID(@PathVariable("companyId") UUID companyId) {
        companyService.deleteCompany(companyId);
    }

    @PutMapping
    @RequestMapping(path = "/upd/{companyId}")
    public void updateCompany(@PathVariable("companyId") UUID companyId,@Valid @NonNull @RequestBody Company companyToUpdate) {
        companyService.updateCompany(companyId, companyToUpdate);
    }
}
