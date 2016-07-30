package net.precursorsbombs.serverlogic.mapObjects;

/**
 * Created by jam414 on 24/05/16.
 */
public enum BlockTypes {

  IND ("Indestructible"), // brick
  DES ("Destructible"), // wall
  EMP ("Empty"),
  BMB ("Bomb"),
  POWER_UP ("PowerUp"),
  CELL ("Cell");

  private final String type;
  BlockTypes(String type){
    this.type = type;
  }

  public String getType(){
    return type;
  }


}
