package com.example;

import java.util.List;
import java.util.Optional;

public interface MoonMissionRepository {
    // List moon missions (prints spacecraft names from `moon_mission`).
    List<String> spacecraftNames();

    // Get a moon mission by mission_id (prints details for that mission).
    // Optional to not get NullPointerException
    Optional<MoonMissionObj> findMoonMission(long missionId);

    // Count missions for a given year (prompts: year; prints the number of missions launched that year)
    int countMissionsByYear(int year);
}
