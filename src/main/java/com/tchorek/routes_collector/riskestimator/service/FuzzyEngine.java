package com.tchorek.routes_collector.riskestimator.service;

import com.fuzzylite.Engine;
import com.fuzzylite.imex.FllImporter;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;
import com.tchorek.routes_collector.riskestimator.model.FuzzyModel;
import com.tchorek.routes_collector.utils.RiskLevel;
import org.springframework.stereotype.Component;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Set;

import static java.lang.StrictMath.round;

@Component
public class FuzzyEngine {

    private Engine engine;
    private InputVariable placteDeltaTime;
    private InputVariable meetingDeltaTime;
    private OutputVariable dangerlvl;

    public FuzzyEngine() throws URISyntaxException {
        URL res = getClass().getClassLoader().getResource("fuzzylogic.fll");
        File file = Paths.get(res.toURI()).toFile();
        StringBuilder status = new StringBuilder();
        try {
            this.engine = new FllImporter().fromFile(file);
            engine.isReady(status);
        } catch (RuntimeException | IOException e1) {
            System.err.println("Engine can not start.");
        }


        placteDeltaTime = engine.getInputVariable("meetingDeltaTime");
        meetingDeltaTime = engine.getInputVariable("placeDeltaTime");
        dangerlvl = engine.getOutputVariable("dangerlvl");
    }

    public void estimateRiskPossibility(Set<FuzzyModel> fetchedUsers) {
        fetchedUsers.forEach(user ->
        {
            this.placteDeltaTime.setValue(user.getDeltaAtPlace());
            this.meetingDeltaTime.setValue(user.getDeltaBetweenMeetings());
            this.engine.process();

            double value = this.dangerlvl.getValue();
            System.out.println("meetingDeltaTime "+user.getDeltaBetweenMeetings()+" placeDeltaTime "+ user.getDeltaAtPlace()+" risk "+value);

            for (RiskLevel riskLevel: RiskLevel.values()) {
                if (riskLevel.getValue() == round(value)) {
                    user.setRiskLevel(riskLevel);
                }
            }
        });
    }
}
