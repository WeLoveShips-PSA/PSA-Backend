package com.example.PSABackend.DAO;

import com.example.PSABackend.classes.Company;
import com.example.PSABackend.classes.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface CompanyDAO {

    int addCompany(UUID companyId, Company company); // if the user is given an id

    default int addCompany(Company company) {
        UUID companyId = UUID.randomUUID(); // generates our own UUID
        return addCompany(companyId, company);
    }

    List<Company> selectAllCompanies();

    Optional<Company> selectCompanyById(UUID companyId);

    int deleteCompanyById(UUID companyId);

    int updateCompanyById(UUID companyId, Company company);
}

