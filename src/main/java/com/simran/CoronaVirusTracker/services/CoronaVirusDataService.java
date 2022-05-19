package com.simran.CoronaVirusTracker.services;

import com.simran.CoronaVirusTracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/*get the data from the Github repo and then parse it*/

@Service
public class CoronaVirusDataService {

    private List<LocationStats> allStats=new ArrayList<>();

    public List<LocationStats> getAllStats() {
        return allStats;
    }

    private static String VIRUS_DATA_URL="https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
   //this url needs to make the HTTP call using HTTP client available from Java 11+

    @PostConstruct //after Constructing the Instance of the Service execute this method
    @Scheduled(cron = "* * * * * *")// scheduled to run this method every second
    public  void  fetchVirusData() throws IOException, InterruptedException {

         List<LocationStats> newStats=new ArrayList<>();
      //because of concurrency reasons, alot of users are trying to access the repo and we dont want to give errors , we will populate allStats with newStats after completing
        HttpClient client=HttpClient.newHttpClient(); // new client created
        HttpRequest request= HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATA_URL))
                .build();

        //httpRequest allows us to use the builder and convert the string to Uri



        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());// this will give us the response in the form of the String
        StringReader csvBodyReader = new StringReader(httpResponse.body());// StringReader is an instance of Reader which parses String
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);

       for (CSVRecord record : records) {     //loop and get the header values for each record
           LocationStats locationStats=new LocationStats();
           locationStats.setState(record.get("Province/State"));
           locationStats.setCountry(record.get("Country/Region"));

            locationStats.setLatestTotalCases(Integer.parseInt(record.get(record.size()-1)));
            locationStats.setDiffCases(Integer.parseInt(record.get(record.size()-1))-Integer.parseInt(record.get(record.size()-2)));
            newStats.add(locationStats);
       }
       this.allStats=newStats;

    }


}
