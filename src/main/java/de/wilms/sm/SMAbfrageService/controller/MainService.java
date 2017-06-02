package de.wilms.sm.SMAbfrageService.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2017
 */
@RestController
public class MainService {

    @GetMapping("/")
    public String main(){
        return "Hello! Please go to <a href=\"https://sm-abfrage.herokuapp.com/datavolume\">this site<a> to see your datavolume!";
    }
}
