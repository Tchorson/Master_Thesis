package com.tchorek.routes_collector.riskestimator.service;

import com.tchorek.routes_collector.database.model.HistoryTracks;
import com.tchorek.routes_collector.database.model.LightHistoryTrack;
import com.tchorek.routes_collector.database.repositories.HistoryTrackRepository;
import com.tchorek.routes_collector.riskestimator.model.FuzzyModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.tchorek.routes_collector.utils.RiskLevel.UNKNOWN;

@Service
public class RiskEstimatorService {

    private HistoryTrackRepository historyTrackRepository;
    private FuzzyEngine fuzzyEngine;

    @Autowired
    public RiskEstimatorService(HistoryTrackRepository historyTrackRepository, FuzzyEngine fuzzyEngine) {
        this.historyTrackRepository = historyTrackRepository;
        this.fuzzyEngine = fuzzyEngine;
    }

    public Set<FuzzyModel> findEndangeredPeople(String phone, long startDate, long stopDate) {
        Set<FuzzyModel> fetchedUsers = findAllRelatedPersons(phone, startDate, stopDate);
        fuzzyEngine.estimateRiskPossibility(fetchedUsers);
        return fetchedUsers;
    }

    private Set<FuzzyModel> findAllRelatedPersons(String phone, long startDate, long stopDate) {
        byte hierarchy = 0;
        Map<String, FuzzyModel> allRelatedUsers = new LinkedHashMap<>();
        Set<LightHistoryTrack> firstRelatedUsers = historyTrackRepository.getUsers(phone, startDate, stopDate);
        removeDuplicates(firstRelatedUsers);
        recursiveRelatedUsersSearch(phone, firstRelatedUsers, allRelatedUsers, hierarchy, startDate, stopDate, startDate);
        return new LinkedHashSet<>(allRelatedUsers.values());
    }

    private void removeDuplicates(Set<LightHistoryTrack> users) {
        users.forEach(user -> {
            users.forEach(possibleDuplicateUser -> {
                if (possibleDuplicateUser.getName().equals(user.getName())) {
                    if (possibleDuplicateUser.getDate() != user.getDate() || possibleDuplicateUser.getDevice().equals(user.getDevice())) {
                        if (possibleDuplicateUser.getDate() > user.getDate()) {
                            users.remove(possibleDuplicateUser);
                        }
                    }
                }
            });
        });
    }

    private void recursiveRelatedUsersSearch(String previousMetUser, Set<LightHistoryTrack> upperRelatedUsers, Map<String, FuzzyModel> allRelatedUsers,
                                             byte hierarchy, long startDate, long stopDate, long initialDate) {
        byte level = (byte) (hierarchy + 1);
        if (level >= 4)
            return;
        upperRelatedUsers.forEach(
                relatedUser ->
                {
                    String name = relatedUser.getName();
                    long recentMeetingTime = relatedUser.getDate();
                    Set<LightHistoryTrack> intermediateRelatedUsers = historyTrackRepository.getUsers(name, recentMeetingTime, stopDate);
                    removeDuplicates(intermediateRelatedUsers);
                    HistoryTracks recentMeeting = historyTrackRepository.getUserWhoMetUserRecently(previousMetUser, relatedUser.getDevice(), relatedUser.getDate());
                    if (recentMeeting == null) {
                        System.out.println("User has not met anyone during the chosen period of time");
                        return;
                    }
                    long meetingTimeBeforeNow = recentMeeting.getDate();
                    if (!intermediateRelatedUsers.isEmpty())
                        recursiveRelatedUsersSearch(name, intermediateRelatedUsers, allRelatedUsers, level, recentMeetingTime, stopDate, initialDate);
                    if (allRelatedUsers.containsKey(name)) {
                        FuzzyModel model = allRelatedUsers.get(name);
                        if (model.getHierarchyLevel() > level || model.getHierarchyLevel() == level && recentMeetingTime - meetingTimeBeforeNow < model.getDeltaBetweenMeetings()) {
                            allRelatedUsers.put(name, new FuzzyModel(name, recentMeetingTime - meetingTimeBeforeNow, recentMeetingTime - startDate, level, UNKNOWN));
                        }
                    } else {
                        if (startDate == initialDate){
                            allRelatedUsers.put(name, new FuzzyModel(name, recentMeetingTime - meetingTimeBeforeNow, 0, level, UNKNOWN));
                        }
                        else{
                            allRelatedUsers.put(name, new FuzzyModel(name, recentMeetingTime - meetingTimeBeforeNow, recentMeetingTime - startDate, level, UNKNOWN));
                        }
                    }
                }
        );

    }
}
