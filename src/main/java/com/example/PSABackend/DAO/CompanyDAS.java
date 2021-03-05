package com.example.PSABackend.DAO;

import com.example.PSABackend.classes.Company;
import com.example.PSABackend.classes.User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository("pregress")
public class CompanyDAS implements CompanyDAO {
    private static List<Company> DB = new ArrayList<Company>();

    @Override
    public int addCompany(UUID companyId, Company company) {
        DB.add(new Company(companyId, company.getCompanyName()));
        return 1;
    }

    @Override
    public List<Company> selectAllCompanies() { return DB; }

    @Override
    public Optional<Company> selectCompanyById(UUID companyId) {
        return DB.stream()
                .filter(company -> company.getID().equals(companyId))
                .findFirst();
    }

    @Override
    public int deleteCompanyById(UUID companyId) {
        Optional<Company> companyToDelete = selectCompanyById(companyId);
        if (companyToDelete.isEmpty()) {
            return 0;
        }
        DB.remove((companyToDelete.get()));
        return 1;
    }

    @Override
    public int updateCompanyById(UUID companyId, Company newCompany) {
        return selectCompanyById(companyId)
                .map(company -> {
                    int indexOfCompanyToUpdate= DB.indexOf(company);
                    if (indexOfCompanyToUpdate >= 0) { // means we got a user to delete
                        DB.set(indexOfCompanyToUpdate, new Company(companyId, company.getCompanyName()));
                        return 1;
                    }
                    return 0;
                })
                .orElse(0);
    }
}
