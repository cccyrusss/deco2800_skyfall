package deco2800.skyfall.managers.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class InsertDataQueries {

    private Connection connection;

    public InsertDataQueries(Connection connection) {
        this.connection = connection;
    }

    public void insertSave(long id, String data) throws SQLException{
        String query = String.format(, id, data);
        PreparedStatement preparedStatement = connection.prepareStatement("insert into SAVES (save_id, data) values (?, ?)");

        statement.executeUpdate(query);
    }

    public void insertWorld(long saveId, long worldId, boolean isCurrentWorld, String data) throws SQLException{
        String query = String.format("insert into WORLDS (save_id, world_id, is_current_world, data) values (%s,%s,"
                + "%s,'%s')", saveId, worldId, isCurrentWorld, data);
        statement.executeUpdate(query);
    }

    public void insertMainCharacter(long id, long saveId, String data) throws SQLException{
        String query = String.format("insert into MAIN_CHARACTER (character_id, save_id, data) values (%s,%s,'%s')", id
            , saveId, data);
        statement.executeUpdate(query);
    }

    public void insertNodes(long worldId, double xPos, double yPos, String data, long nodeId, long biomeId) throws SQLException{
        String query = String.format("insert into NODES (node_id, world_id, x_pos, y_pos, data, biome_id) values (%s, %s, %s, "
                + "'%s',%s , %s)", nodeId, worldId, xPos, yPos, data, biomeId);
        statement.executeUpdate(query);
    }

    public void insertEdges(long worldID, long edgeID, int biomeID, String data) throws SQLException{
        String query = String.format("insert into EDGES (world_id, edge_id, biome_id, data) values (%s,%s,%s,'%s')", worldID,
            edgeID, biomeID, data);
        statement.executeUpdate(query);
    }

    public void insertBiome(int biomeId, long worldId, String biomeType, String data) throws SQLException{

        String query = String.format("insert into BIOMES (biome_id, world_id, biome_type, 'data) "
            + "values (%s,%s, '%s', '%s')", biomeId, worldId, biomeType, data);
        statement.executeUpdate(query);
    }

    public void insertChunk(long worldId, int x, int y, String data) throws SQLException{
        String query = String.format("insert into CHUNKS (world_id, x, y, data) values (%s,%s,%s,'%s')", worldId, x, y,
            data);
        statement.executeUpdate(query);
    }

    public void insertEntity(String type, double x, double y, int chunkX, int chunkY, long worldId, String data, long entityId) throws SQLException {
        String query = String.format("insert into ENTITIES (type, x, y, chunk_x, chunk_y, world_id, data, entity_id) values (%s,"
                + "%s,%s,%s,%s,%s,'%s')", type, x, y, chunkX, chunkY, worldId, data, entityId);
        statement.executeUpdate(query);
    }
}