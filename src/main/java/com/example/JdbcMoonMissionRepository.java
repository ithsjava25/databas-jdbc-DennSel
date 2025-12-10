package com.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcMoonMissionRepository implements MoonMissionRepository {

    private final DataSource dataSource;

    public JdbcMoonMissionRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<String> spacecraftNames() {
        List<String> spaceCraftNames = new ArrayList<>();
        String query = "select spacecraft from moon_mission";

        // Try-with to have it auto close
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                spaceCraftNames.add(rs.getString("spacecraft"));
            }

            return spaceCraftNames;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<MoonMissionObj> findMoonMission(long missionId) {
        String query = "select * from moon_mission where mission_id = ?";

        // Try with to have it auto close
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, missionId);

            // Try with to have it auto close
            try (ResultSet rs = statement.executeQuery()) {
                // See if there's a match, return
                if (rs.next()) {
                    MoonMissionObj moonMissionObj = new MoonMissionObj(
                            rs.getLong("mission_id"),
                            rs.getString("spacecraft"),
                            rs.getDate("launch_date").toLocalDate(),
                            rs.getString("carrier_rocket"),
                            rs.getString("operator"),
                            rs.getString("mission_type"),
                            rs.getString("outcome")
                    );
                    // If mission found
                    return Optional.of(moonMissionObj);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            // If no mission found
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int countMissionsByYear(int year) {
        String query = "select count(*) from moon_mission where year(launch_date) = ?";

        // Try with to have it auto close
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, year);

            // Try with to have it auto close
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            return 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
