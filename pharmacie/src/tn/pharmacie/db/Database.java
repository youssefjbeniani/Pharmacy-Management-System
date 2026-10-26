package tn.pharmacie.db;

// ===== IMPORTS =====
import java.sql.Connection;      
import java.sql.DriverManager;   
import java.sql.Statement;       


public class Database {

    
    private static final String URL = "jdbc:sqlite:pharmacie.db";

   
    public static Connection getConnection() throws Exception {

       

       
        Connection c = DriverManager.getConnection(URL);

        
        try (Statement st = c.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON;");
        }

       
        return c;
    }
}
