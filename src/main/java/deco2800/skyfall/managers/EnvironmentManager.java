package deco2800.skyfall.managers;

import deco2800.skyfall.entities.AbstractEntity;
import deco2800.skyfall.worlds.*;

import java.util.List;

public class EnvironmentManager {

   //Hours in a game day
   private long hours;

   //Day/Night tracker
   private boolean isDay;

   /**
    * Constructor
    *
    */
   public EnvironmentManager(long i) {

      //Each day cycle goes for approx 24 minutes
      long time = (i / 60000);
      hours = time % 24;

      //Set Day/Night tracker
      if (hours > 12 && hours < 24) {
         isDay = false;
      } else {
         isDay = true;
      }
   }

   /**
    * Gets time of day in game
    *
    * @return long The time of day
    */
   public long getTime() {
      return hours;
   }

   /**
    * Sets the time of day in game
    *
    * @param time The time of day to be set
    */
   public void setTime(long time) {
      if (time > 24) {
         hours = 24;
      } else {
         hours = time;
      }
      setDay();
   }

   /**
    * Sets day/night tracker after updating time
    */
   private void setDay() {
      if (hours > 12 && hours < 24) {
         isDay = false;
      } else {
         isDay = true;
      }
   }

   /**
    * Returns whether it is day or not
    * @return boolean True if it is day, False if night
    */
   public boolean isDay() {
      return isDay;
   }

   /**
    * TODO for sprint 2
    */
   public void setBiomeMusic() {
      //get biome and play music accordingly
   }
}