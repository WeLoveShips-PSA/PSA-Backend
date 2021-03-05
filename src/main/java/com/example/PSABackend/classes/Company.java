package com.example.PSABackend.classes;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

public class Company {

    private final UUID companyId;
    @NotBlank
    private String companyName;

    public Company(@JsonProperty("companyId") UUID companyId, @JsonProperty("companyName") String companyName) {
        this.companyId = companyId;
        this.companyName = companyName;
    }

    public UUID getID() { return companyId; }

    public String getCompanyName() { return companyName; }



}
