package Terrain;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class TerrainGenerator {

    private static final String DB_URL = "jdbc:mysql://raptor2.aut.ac.nz:3306/terrains";

    public static Grid loadTerrain(String terrainName) {
        Grid returnGrid = null;
        Random rand = new Random();
        
        int x = 0;
        int y = 0;
        
        switch(terrainName) {
            case "5x5":
                x = 5;
                y = 5;
                returnGrid = new Grid(x, y);
                returnGrid.setBoxSize(50);
                break;
                
            case "40x50":
                x = 50;
                y = 40;
                returnGrid = new Grid(x, y);
                returnGrid.setBoxSize(15);
                break;
                
            case "20x30":
                x = 30;
                y = 20;
                
                returnGrid = new Grid(x, y);
                returnGrid.setBoxSize(25);
                break;
                
            case "10x10":
                x = 10;
                y = 10;
                
                returnGrid = new Grid(x, y);
                returnGrid.setBoxSize(50);
                break;
                
            case "7x4":
                x = 4;
                y = 7;
                
                returnGrid = new Grid(x, y);
                returnGrid.setBoxSize(50);
                break;
                
            case "3x3":
                x = 3;
                y = 3;
                
                returnGrid = new Grid(x, y);
                returnGrid.setBoxSize(50);
                break;
        }
        
        /* Used for database terrain
        try {
            Connection conn = DriverManager.getConnection(DB_URL, "student", "fpn871");
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("select * from " + terrainName);

            while(rs.next()) {
                if(returnGrid != null) {
                    returnGrid.setQuadrantValue(Integer.parseInt(rs.getString(1)), Integer.parseInt(rs.getString(2)), Integer.parseInt(rs.getString(3)));
                }
            }

        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        */
        for(int k = 0; k < x; k++) {
            for(int i= 0; i < y; i++) {
                returnGrid.setQuadrantValue(i, k, rand.nextInt(20)-5);
            }
        }

        return returnGrid;
    }
}
