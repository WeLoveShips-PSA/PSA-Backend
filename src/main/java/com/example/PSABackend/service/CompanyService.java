package com.example.PSABackend.service;

import com.example.PSABackend.DAO.CompanyDAO;
import com.example.PSABackend.classes.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CompanyService {
    private final CompanyDAO companyDAO;

    @Autowired
    public CompanyService(@Qualifier("pregress") CompanyDAO companyDAO) {this.companyDAO = companyDAO; }

    public int addCompany(Company company) {return companyDAO.addCompany(company); }

    public List<Company> getAllCompanies() { return companyDAO.selectAllCompanies(); }

    public Optional<Company> getCompanyById(UUID companyId) { return companyDAO.selectCompanyById(companyId); }

    public int deleteCompany(UUID companyId) { return companyDAO.deleteCompanyById(companyId); }

    public int updateCompany(UUID companyId, Company newCompany) { return companyDAO.updateCompanyById(companyId, newCompany); }
}
