package com.simran.CoronaVirusTracker.controller;


import com.simran.CoronaVirusTracker.models.LocationStats;
import com.simran.CoronaVirusTracker.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller //render HTML ui thats why not restcontroller
public class HomeController {

    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")
    public String home(Model model){
        List<LocationStats> allStats=coronaVirusDataService.getAllStats();
        int totalCases=allStats.stream().mapToInt(stat-> stat.getLatestTotalCases()).sum();
        int totalnewCases=allStats.stream().mapToInt(stat-> stat.getDiffCases()).sum();

        model.addAttribute("locationStats",allStats);
        model.addAttribute("totalCases",totalCases);
        model.addAttribute("totalnewCases",totalnewCases);


        return "index";
    }
}
