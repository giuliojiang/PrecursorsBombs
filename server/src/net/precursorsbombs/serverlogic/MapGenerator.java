package net.precursorsbombs.serverlogic;

import net.precursorsbombs.serverlogic.mapObjects.BlockTypes;
import net.precursorsbombs.serverlogic.mapObjects.Cell;

import java.util.*;

/**
 * Created by jam414 on 30/05/16.
 */
public class MapGenerator{

  //#, .,

  private static final int DES_PROB = 11;
  private static final int IND_PROB = 4;
  private static final int EMP_PROB = 1;
  private static final int DIST = 10;



  private final int numPlayers;
  private final int mapSize;
  private final int numLevels;
  private final Random random;




  public MapGenerator(int numPlayers, int mapSize, int numLevels){
    this.numPlayers = numPlayers;
    this.mapSize = mapSize;
    this.numLevels = numLevels;
    this.random = new Random();
  }

  public List<Map> generateMap(){
    List<char[][]> level = new ArrayList<>(numLevels);
    char[][] mapSet = firstLevel();
    level.add(mapSet);

    for(int i = 1; i < numLevels; i++){
      mapSet = addLevel(i * 2);
      level.add(mapSet);
    }







    return null;
  }

  private char[][] firstLevel(){
    char[][] level = new char[mapSize][mapSize];

    for (int i = 0; i < mapSize; i++){
      for (int j = 0; j < mapSize; j++){

        if (i == 0 || j == 0 || j == mapSize - 1 || i == mapSize - 1){
          level[i][j] = '#';
          continue;
        }

        int rand = random.nextInt(DIST) + 1;

        if (rand == EMP_PROB){
          level[i][j] = ' ';
        }
        else if (rand < IND_PROB){
          level[i][j] = '#';
        }
        else if (rand < DES_PROB){
          level[i][j] = '.';
        }

      }
    }

    return level;
  }

  private char[][] addLevel(int height){
    int levelSize = mapSize - height;
    char[][] level = new char[levelSize][levelSize];

    for (int i = 0; i < levelSize; i++){
      for (int j = 0; j < levelSize; j++){

        int rand = random.nextInt(DIST) + 1;

        if (rand == EMP_PROB){
          level[i][j] = ' ';
        }
        else if (rand < IND_PROB){
          level[i][j] = '#';
        }
        else if (rand < DES_PROB){
          level[i][j] = '.';
        }

      }
    }

    return level;
  }

  private BlockTypes[][] parseGenMap (char[][] charMap) {
    BlockTypes[][] map = new BlockTypes[mapSize][mapSize];

      for (int i = 0; i < mapSize; i++) {
        for (int j = 0; j < mapSize; j++) {
          char c = charMap[i][j];
          BlockTypes block;
          if (c == '#') block = BlockTypes.IND;
          else if (c == '.') block = BlockTypes.DES;
          else block = BlockTypes.EMP;
          map[i][j] = block;
        }
      }
    return map;
  }


}
