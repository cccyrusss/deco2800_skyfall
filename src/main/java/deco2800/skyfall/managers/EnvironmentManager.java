package deco2800.skyfall.managers;

import deco2800.skyfall.entities.AbstractEntity;
import deco2800.skyfall.worlds.Tile;

import java.util.List;

public class EnvironmentManager extends TickableManager {

   //Hours in a game day
   public long hours;

   // Seconds in a game day
   public long  minutes;

   // Seasons in game
   private String season;

   // Month in game
   private long month;

   // Seasons in game
   private long day;

   // Day/Night tracker
   private boolean isDay;

   // Biome player is currently in
   private String biome;

   // Time to display on screen
   long displayHours;

   // Time of day: AM or PM
   public String TOD;

   // Music filename
   public String file;

   // Current music file being played
   private String currentFile;

   // Background Music Manager
   private BGMManager bgmManager;

   /**
    * Constructor
    *
    */
   public EnvironmentManager() {
      file = "resources/sounds/forest_day.wav";
      currentFile = "resources/sounds/forest_night.wav";
   }

   /**
    * Private helper function for constructor to set biome
    */
   public void setBiome() {
      List<AbstractEntity> entities = GameManager.get().getWorld().getEntities();
      AbstractEntity player;
      for (int i = 0; i < entities.size(); i++) {
         if (entities.get(i).getObjectName().equals("playerPeon")) {
            player = entities.get(i);
            Tile currentTile = GameManager.get().getWorld().getTile(player.getCol(), player.getRow());
            // If player coords don't match tile coords, currentTile returns null
            // eg if player isn't exactly in the middle of a tile (walking between tiles), coords don't match
            // So below if statement is needed
            if (currentTile != null) {
               biome = currentTile.getBiome().getBiomeName();
            } else {
               // do nothing
            }
         }
      }
   }

   /**
    * Gets current biome player is in
    *
    * @return String Current biome of player, or null if player is moving between tiles
    */
   public String currentBiome() {
      return biome;
   }

   /**
    * Gets time of day in game
    *
    * @return long The time of day in hours
    */
   public long getTime() {
      return hours;
   }

   /**
    * Sets the time of day in game
    *
    * @param i The time of day to be set
    */
   public void setTime(long i) {
      //Each day cycle goes for approx 24 minutes
      long timeHours = (i / 60000);
      hours = timeHours % 24;

      //Each minute equals one second
      long timeMins = (i / 1000);
      minutes = timeMins % 60;
   }

   /**
    * Returns whether it is day or not
    * @return boolean True if it is day, False if night
    */
   public boolean isDay() {

      // Day equals am, night equals pm for now.
      if (hours >= 12 && hours < 24) {
         isDay = false;
      } else {
         isDay = true;
      }

      return isDay;
   }

   /**
    * Returns whether it is day or not
    * @return String am or pm depending on TOD
    */
   public String getTOD() {
      // Set hours to be displayed
      if (hours > 12 && hours < 24) {
         displayHours = hours - 12;
         TOD = "pm";
      } else if (hours == 24) {
         displayHours = hours - 12;
         TOD = "am";
      } else if (hours == 12) {
         displayHours = hours;
         TOD = "pm";
      } else {
         displayHours = hours;
         TOD = "am";
      }

      if (minutes < 10) {
         return Long.toString(displayHours) + ":" + "0" + Long.toString(minutes) + TOD;
      }

      return Long.toString(displayHours) + ":" + Long.toString(minutes) + TOD;
   }

   /**
    * Sets the season in game, starting with summer.
    * @param i the time in milliseconds
    */
   public void setMonth(long i) {
      //Each month goes for approx 30 days
      long timeMonth = (i/60000)/730;
      month = timeMonth % 12;
   }

   /**
    * Gets time of day in game
    *
    * @return String The month
    */
   public String getSeason() {
      String seasonString;

      switch (((int) month)) {
         case 1:  seasonString = "Summer";
            break;
         case 2:  seasonString = "Summer";
            break;
         case 3:  seasonString = "Autumn";
            break;
         case 4:  seasonString = "Autumn";
            break;
         case 5:  seasonString = "Autumn";
            break;
         case 6:  seasonString = "Winter";
            break;
         case 7:  seasonString = "Winter";
            break;
         case 8:  seasonString = "Winter";
            break;
         case 9:  seasonString = "Spring";
            break;
         case 10: seasonString = "Spring";
            break;
         case 11: seasonString = "Spring";
            break;
         case 12: seasonString = "Summer";
            break;
         default: seasonString = "Invalid season";
            break;
      }

      return seasonString;
   }

   /**
    * Sets the filename in game
    *
    */
   public void setFilename() {
      String[] arrOfStr = file.split("_", 4);

      // Check time of day and change files accordingly
      if(isDay() == true) {
         file = "resources/sounds/forest_" + arrOfStr[1];

      } else {
         arrOfStr[1] = "night.wav";
         file = "resources/sounds/forest_" + arrOfStr[1];
      }
   }

   /**
    * Sets the music in game
    */
   public void setTODMusic () {

      if (!(file.contains(currentFile))) {
         setFilename();

         // Stop current music
         try {
            bgmManager.stop();
         } catch (Exception e) {
            // Exception caught, if any
         }

         currentFile = file;

         // Play BGM
         try {
            bgmManager.initClip(currentFile);
            bgmManager.play();
         } catch (Exception e) {
            // Exception caught, if any
         }
      }

      setFilename();

   }

   /**
    * On tick method for ticking managers with the TickableManager interface
    *
    * @param i
    */
   @Override
   public void onTick(long i) {
      long time = i;

      if (System.currentTimeMillis() - time > 20) {
         time = System.currentTimeMillis();
      }

      // Set the TOD and month in game
      setTime(time);
      setMonth(time);

      //Set Background music
      setTODMusic();
   }
   
}
