package deco2800.skyfall.managers;

import deco2800.skyfall.entities.*;
import deco2800.skyfall.entities.worlditems.*;
import deco2800.skyfall.managers.database.DataBaseConnector;
import deco2800.skyfall.worlds.world.World;
import deco2800.skyfall.worlds.Tile;
import deco2800.skyfall.util.HexVector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.lang.reflect.InvocationTargetException;

public final class DatabaseManager extends AbstractManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private static final String TEXTURESTRING = "texture";
    private static final String ROWPOSSTRING = "rowPos";
    private static final String COLPOSSTRING = "colPos";
    private static String saveName = "";
    private static List<String> saveNameList = new ArrayList<>();

    //The current instance of the DataBaseManager
    private static DatabaseManager instance = null;

    //An instance of DataBaseConnector that is used to connect and manage data in the database
    private static DataBaseConnector dataBaseConnector;

    private DatabaseManager() {
        /*
         This constructor is not called, but added to deal with the:
             Add a private constructor to hide the implicit public one.
         code smell
        */
    }

    /**
     * This function will generate the JSON for a tile, and return the StringBuilder with the JSON appended.
     *
     * @param t the tile we are generating the JSON for
     * @param entireJsonAsString the entire JSON as it currently exists
     * @param appendComma a boolean letting us know whether we're at the end of the JSON array.
     * @return A StringBuilder object with the tile JSON appended.
     */
    private static StringBuilder generateJsonForTile(Tile t, StringBuilder entireJsonAsString, boolean appendComma) {
        Gson tileJson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Float rowPosition = t.getRow();
        Float colPosition = t.getCol();
        String json = tileJson.toJson(t);

        JsonElement element = tileJson.fromJson(json, JsonElement.class);
        JsonObject jsonObject = element.getAsJsonObject();
        JsonObject result = new JsonObject();

        for (String s : jsonObject.keySet()) {
            result.add(s, jsonObject.get(s));
        }
        result.addProperty(ROWPOSSTRING, rowPosition);
        result.addProperty(COLPOSSTRING, colPosition);
        String finalJson = result.toString();
        entireJsonAsString.append(finalJson);
        if (appendComma) {
            entireJsonAsString.append(",");
        }
        return entireJsonAsString;
    }

    /**
     * This function will generate the JSON for an entity, and return the StringBuilder with the JSON appended.
     *
     * @param e the entity we are generating the JSON for
     * @param entireJsonAsString the entire JSON as it currently exists
     * @param appendComma a boolean letting us know whether we're at the end of the JSON array.
     * @return A StringBuilder object with the entity JSON appended.
     */
    private static StringBuilder generateJsonForEntity(AbstractEntity e, StringBuilder entireJsonAsString,
            boolean appendComma) {
        Gson tileJson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Float rowPosition = e.getRow();
        Float colPosition = e.getCol();
        String json = tileJson.toJson(e);

        JsonElement element = tileJson.fromJson(json, JsonElement.class);
        JsonObject jsonObject = element.getAsJsonObject();
        JsonObject result = new JsonObject();

        result.addProperty("objectName", e.getObjectName());
        for (String s : jsonObject.keySet()) {
            result.add(s, jsonObject.get(s));
        }
        result.addProperty(ROWPOSSTRING, rowPosition);
        result.addProperty(COLPOSSTRING, colPosition);

        String finalJson = result.toString();
        entireJsonAsString.append(finalJson);
        if (appendComma) {
            entireJsonAsString.append(",");
        }
        return entireJsonAsString;
    }

    /** This function will return the string represented by a GSON token.
     *
     * @param reader the JsonReader that tracks where we ar ein the file.
     * @return The string of that token.
     */
    private static String readGsonToken(JsonReader reader) {
        try {
            return reader.nextName();
        } catch (IOException e) {
            logger.error("JsonReader isn't findable/readable");
        }
        return "Error";
    }

    /**
     * This function will read the outer JSON file, reading the main array names [entities or tiles]
     * If we are past these two tokens, the BreakSignal will be returned.
     *
     * @param reader the JsonReader that tracks where we are in the file
     * @return A string representing the main array name
     */
    private static String readOuterJson(JsonReader reader) {
        try {
            return readGsonToken(reader);
        } catch (IllegalStateException e) {
            // This catch is used to figure out if we're finished loading entities and tiles.
            try {
                if (reader.peek() == JsonToken.END_DOCUMENT) {
                    return "BreakSignal";
                } else {
                    logger.error("The json save file was not loaded entirely");
                }
            } catch (IOException ioException) {
                logger.error("JsonReader isn't findable/readable");
            }
        }
        return "ErrorSignal";
    }

    /**
     * Processes the tile portions of the JSON file when loading.
     *
     * @param reader the JsonReader object for loading JsonTokens
     * @param newTiles the list of new tiles.
     */
    private static void processTileJson(JsonReader reader, List<Tile> newTiles) {
        try {
            reader.nextName();
            reader.beginArray();
            while (reader.hasNext()) {
                reader.beginObject();

                // TODO This looks broken.
                // Tile tile = new Tile(0, 0);
                Tile tile = null;
                while (reader.hasNext()) {
                    checkBasicTileSettings(tile, reader.nextName(), reader);
                }

                newTiles.add(tile);
                reader.endObject();
            }
            reader.endArray();
            reader.endObject();
        } catch (IOException e) {
            logger.error("Cannot read the tile json array");
        }
    }

    private static boolean checkBasicTileSettings(Tile tile, String entityField, JsonReader reader) {
        try {
            switch (entityField) {
            case "colPos":
                tile.setCol((float) reader.nextDouble());
                return true;
            case "rowPos":
                tile.setRow((float) reader.nextDouble());
                return true;
            case "index":
                tile.setIndex(reader.nextInt());
                return true;
            case "texture":
                tile.setTexture(reader.nextString());
                return true;
            case "tileID":
                tile.setTileID(reader.nextInt());
                return true;
            case "obstructed":
                tile.setObstructed(reader.nextBoolean());
                return true;
            default:
                logger.error("Unexpected attribute when loading an entity:" + entityField);
                return false;
            }
        } catch (IOException e) {
            logger.error("Cannot read the tile json array");
        }
        return false;
    }

    private static AbstractEntity resolveEntityToLoad(String entityObjectName) {
        try {
            for (String s : Arrays.asList("rock")) {
                if (entityObjectName.startsWith(s)) {
                    Rock create = new Rock();
                    create.setObjectName(entityObjectName);
                    return (AbstractEntity) create;
                }
            }

            for (String s : Arrays.asList("staticEntityID")) {
                if (entityObjectName.startsWith(s)) {
                    StaticEntity create = new StaticEntity();
                    create.setObjectName(entityObjectName);
                    return (AbstractEntity) create;
                }
            }

            for (String s : Arrays.asList("playerPeon")) {
                if (entityObjectName.startsWith(s)) {
                    PlayerPeon create = new PlayerPeon(1, 1, 1, "playerPeon", 10);
                    create.setObjectName(entityObjectName);
                    return (AbstractEntity) create;
                }
            }

            StringBuilder fullEntityName = new StringBuilder();
            fullEntityName.append("deco2800.skyfall");
            HashMap<String, String> entityMap = new HashMap<>();
            entityMap.put("player", "entities.PlayerPeon");
            entityMap.put("rock", "entities.rock");
            entityMap.put("tree", "entities.Tree");
            entityMap.put("staticEntityID", "entities.StaticEntity");

            fullEntityName.append(entityMap.get(entityObjectName));
            return (AbstractEntity) Class.forName(fullEntityName.toString()).getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | InvocationTargetException e) {
            return null;
        }
    }

    /**
     * Gets the current DatabaseManger, and if it does exist create it and return it
     * @return A DataBaseManager
     */
    public static DatabaseManager get() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private static AbstractEntity checkBasicEntitySettings(AbstractEntity entity, String entityField,
            JsonReader reader) {
        try {
            switch (entityField) {
            case "speed":
                ((AgentEntity) entity).setSpeed((float) reader.nextDouble());
                return entity;
            case "colPos":
                entity.setCol((float) reader.nextDouble());
                return entity;
            case "rowPos":
                entity.setRow((float) reader.nextDouble());
                return entity;
            case "texture":
                entity.setTexture(reader.nextString());
                return entity;
            case "children":
            case "staticTexture":
                reader.beginObject();
                Map<HexVector, String> children = new HashMap<>();
                while (reader.hasNext()) {
                    JsonToken t = reader.peek();
                    String position = reader.nextName();
                    String texture = reader.nextString();
                    HexVector pos = new HexVector(position);
                    children.put(pos, texture);
                }

                ((StaticEntity) entity).setChildren(children);
                reader.endObject();
                return entity;

            case "entityID":
                entity.setEntityID(reader.nextInt());
                return entity;
            case "obstructed":
                return entity;
            default:
                logger.error("Unexpected attribute when loading an entity:" + entityField);
                return null;
            }
        } catch (IOException e) {
            logger.error("Cannot read the tile json array");
        }
        return null;
    }

    /**
     * Processes the entity portions of the JSON file when loading.
     *
     * @param reader the JsonReader object for loading JsonTokens
     * @param newEntities the map of new entities.
     */
    private static void processEntityJson(JsonReader reader, Map<Integer, AbstractEntity> newEntities) {

        String entityName = "";
        try {
            AbstractEntity entity;
            JsonToken t = reader.peek();
            entityName = reader.nextName();
            if (entityName.startsWith("objectName")) {
                entityName = reader.nextString();
                entity = resolveEntityToLoad(entityName);
                if (entity == null) {
                    logger.error("Unable to resolve an " + entityName + " from the save file, on load.");
                    logger.error("This is likely due to the entity being a new addition to the game.");
                    return;
                }
                entity.setObjectName(entityName);

                while (reader.hasNext()) {
                    entity = checkBasicEntitySettings(entity, reader.nextName(), reader);
                }
                reader.endObject();
                if (entity != null) {
                    newEntities.put(entity.getEntityID(), entity);
                }
            }
        } catch (IOException e) {
            logger.error("Cannot read the tile json array");
        }

    }

    private static boolean startArrayReading(JsonReader reader,
                                             CopyOnWriteArrayList<Tile> newTiles) {
        try {
            reader.beginArray();
            return true;
        } catch (IllegalStateException e) {
            processTileJson(reader, newTiles);
            return false;
        } catch (IOException e) {
            logger.error("Couldn't read from GSON Reader");
            return false;
        }
    }

    private static void descendThroughSaveFile(JsonReader reader, Map<Integer, AbstractEntity> newEntities,
            CopyOnWriteArrayList<Tile> newTiles) {
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = readOuterJson(reader);
                if (name.equals("entities")) {
                    readEntities(reader, newEntities, newTiles);
                } else if (name.equals("BreakSignal")) {
                    break;
                }
            }
        } catch (IOException e) {
            logger.error("Somehow loaded the JSON file, but it's somewhat corrupted", e);
        }
    }

    private static void readEntities(JsonReader reader, Map<Integer, AbstractEntity> newEntities,
            CopyOnWriteArrayList<Tile> newTiles) throws IOException {
        while (reader.hasNext()) {
            if (!startArrayReading(reader, newTiles)) {
                break;
            }
            while (reader.hasNext()) {
                JsonToken nextToken = reader.peek();
                if (JsonToken.BEGIN_OBJECT.equals(nextToken)) {
                    reader.beginObject();
                    processEntityJson(reader, newEntities);
                } else if (JsonToken.NAME.equals(nextToken)) {
                    reader.nextName();
                } else if (JsonToken.STRING.equals(nextToken)) {
                    reader.nextString();
                } else if (JsonToken.NUMBER.equals(nextToken)) {
                    reader.nextDouble();
                } else if (JsonToken.END_OBJECT.equals(nextToken)) {
                    reader.endObject();
                }
            }
            reader.endArray();
        }
    }

    /**
     * This function loads the current state of the world from the save_file.json
     *
     * This function is essentially a whole lot of parsing a JSON, which is particularly cumbersome in Java.
     *
     * If you have any questions please direct them to me, I'll be writing a wiki page about this regardless.
     *
     * @author @shivy
     *
     * @param world We have a world as a parameter for testing purposes.  In the main game, this will never need to be
     *              passed, but when testing a TestWorld is needed to be passed.
     */
    public static void loadWorld(World world) {
        // This check allows for the world parameter to act as an optional
        if (world == null) {
            world = GameManager.get().getWorld();
        }
        String saveLocationAndFilename = "resources/save_file.json";
        File f = new File(saveLocationAndFilename);
        if (!f.exists()) {
            GameManager.get().getManager(OnScreenMessageManager.class)
                    .addMessage("Load attempted, but no save file found");
            logger.info("Load attempted, but no save file found");
        }

        // Load all entities and tiles from the database
        world.queueTilesForDelete(world.getTileMap());
        world.queueEntitiesForDelete(world.getEntities());

        Map<Integer, AbstractEntity> newEntities = new ConcurrentHashMap<>();
        CopyOnWriteArrayList<Tile> newTiles = new CopyOnWriteArrayList<>();

        try {
            JsonReader reader = new JsonReader(new FileReader(saveLocationAndFilename));
            descendThroughSaveFile(reader, newEntities, newTiles);
        } catch (FileNotFoundException e) {
            logger.error("Somehow failed to load the JSON file even after checking", e);
            return;
        }

        world.setTileMap(newTiles);
        // FIXME This is broken, but I haven't fixed given this is going to be overhauled later.
        // world.generateNeighbours();
        world.setEntities(new CopyOnWriteArrayList<>(newEntities.values()));
        logger.info("Load succeeded");
        GameManager.get().getManager(OnScreenMessageManager.class).addMessage("Loaded game from the database.");
    }

    private static void writeToJson(String entireString) {
        BufferedWriter fileWriter = null;
        try {
            Charset charset = Charset.forName("UTF-8");
            Path savePath = FileSystems.getDefault().getPath("resources", saveName);
            java.nio.file.Files.deleteIfExists(savePath);
            fileWriter = Files.newBufferedWriter(savePath, charset);
            fileWriter.write(entireString, 0, entireString.length());
            fileWriter.close();
        } catch (FileNotFoundException exception) {
            logger.error("Save could not write to file.");
        } catch (IOException exception) {
            logger.error("Could not overwrite previous save.");
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                } else {
                    logger.error("Could not close fileWriter as it is null");
                }
            } catch (IOException exception) {
                logger.error("Could not overwrite previous save.");
            } catch (NullPointerException exception) {
                logger.error("Could not write to the file at all.");
            }
        }
    }

    /**
     * This function saves the current state of the world to the Event, Tile, Entity and MultiEntity tables
     *
     * Before saving, the function will delete everything in the Event, Tile, Entity and MultiEntity tables
     *
     * @param world We have a world as a parameter for testing purposes.  In the main game, this will never need to be
     *              passed, but when testing a TestWorld is needed to be passed.
     */
    public static void saveWorld(World world) {
        logger.info("Saving the world to database.");
        // This check allows for world to act as an optional parameter
        if (world == null) {
            world = GameManager.get().getWorld();
        }

        saveName = "save_file.json";

        saveNameList.add(saveName);

        StringBuilder entireJsonAsString = new StringBuilder("{\"entities\": [");

        int entityLength = world.getEntities().size();

        for (int i = 0; i < entityLength; i++) {
            AbstractEntity entity = world.getEntities().get(i);
            if (i == world.getEntities().size() - 1) {
                generateJsonForEntity(entity, entireJsonAsString, false);
            } else {
                generateJsonForEntity(entity, entireJsonAsString, true);
            }
            entireJsonAsString.append('\n');
        }

        entireJsonAsString.append("],");

        entireJsonAsString.append("\"tiles\": [");

        int tileLength = world.getTileMap().size();

        for (int i = 0; i < tileLength; i++) {
            Tile tile = world.getTileMap().get(i);
            if (i == world.getTileMap().size() - 1) {
                generateJsonForTile(tile, entireJsonAsString, false);
            } else {
                generateJsonForTile(tile, entireJsonAsString, true);
            }
            entireJsonAsString.append('\n');
        }

        entireJsonAsString.append("]}");
        writeToJson(entireJsonAsString.toString());
        GameManager.get().getManager(OnScreenMessageManager.class).addMessage("Game saved to the database.");
    }

    /**
     * Creates and starts and new DataBaseConnector
     */
    public void startDataBaseConnector(){
        dataBaseConnector = new DataBaseConnector();
        dataBaseConnector.start();
    }

    /**
     * Closes the connection of the DataBaseConnector to the database
     */
    public void closeDataBaseConnector(){
        dataBaseConnector.close();
    }

    /**
     * Gets the Database connector
     * @return The database connector
     */
    public DataBaseConnector getDataBaseConnector() {
        return dataBaseConnector;
    }



}