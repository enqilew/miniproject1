package miniproject1.cryptocurr.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HistoricalDataController {

    @GetMapping("/historical-data")
    public String historicalDataPage() {
        return "historical-data";
    }
}

