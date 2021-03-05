package com.example.PSABackend.classes;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;

public class Vessel {
    @NotBlank
    private String abbr;
    @NotBlank
    private String voy;
    @NotBlank
    private String bthg;

    private String fullVsim;
    private String invoyn;
}
