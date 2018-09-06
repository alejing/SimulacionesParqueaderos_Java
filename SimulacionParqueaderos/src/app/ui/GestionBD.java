
package app.ui;

import java.sql.*;
import java.util.ArrayList;

public class GestionBD {

    // JDBC driver name and database URL
    private final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private final String DB_URL = "jdbc:mysql://localhost/Apparcar";
    private final String DB_URL_SIM = "jdbc:mysql://localhost/Simulaciones";
    //  Database credentials
    private final String USER = "root";
    private final String PASS = "";

    private Connection conn = null;
    private Statement stmt = null;

    public void actualizarCuposDisponiblesBD
        (int idParqueadero, int cuposDisponibles){

        try{
          //Register JDBC driver
          Class.forName(JDBC_DRIVER);

          //Open a connection
          System.out.println("Conectando a la base de datos...");
          conn = DriverManager.getConnection(DB_URL, USER, PASS);
          System.out.println("Base de datos conectada...");

          //Execute a query
          System.out.println("Actualizando cupos...");
          stmt = conn.createStatement();
          String sql = "UPDATE Parqueadero " +
                       "SET cuposDisponibles = "+ cuposDisponibles +
                       " WHERE idParqueadero = "+idParqueadero ;
          stmt.executeUpdate(sql);
          conn.close();

       }catch(SQLException | ClassNotFoundException se){
          //Handle errors for JDBC
          System.err.println("Error! "); 
          System.err.println(se.getMessage());
       }
        //Handle errors for Class.forName
        finally{
          //finally block used to close resources
          try{
             if(stmt!=null)
                conn.close();
          }catch(SQLException se){
          }// do nothing
          try{
             if(conn!=null)
                conn.close();
          }catch(SQLException se){
             se.printStackTrace();
          }//end finally try
       }//end try
    }
 
    public void insertarParqueaderoSimulacionesBD
        (double latitud, double longitud, int valor, int ofertas, int servicios, int llaves){
            
        try { 
            Class.forName(JDBC_DRIVER);
            System.out.println("Conectando a la base de datos...");
            conn = DriverManager.getConnection(DB_URL_SIM,USER,PASS); 
            System.out.println("Base de datos conectada...");
            stmt = conn.createStatement();
            System.out.println("Ingresando parqueadero...");
            String query = "INSERT INTO Parqueaderos " + 
                "VALUES (null, "+latitud+", "+longitud+", "+valor+", "+ofertas+", "+servicios+", "+llaves+")";
            //System.out.println(query);
            stmt.executeUpdate(query); 
            System.out.println("Parqueadero ingresado ...");
            conn.close(); 
            
        } catch (ClassNotFoundException | SQLException e) { 
            System.err.println("Error! "); 
            System.err.println(e.getMessage()); 
        }  

    }
        
    public ArrayList obtenerParqueaderosSimulacionesBD(){
        
        ParqueaderoSIM p_sim;
        ArrayList<ParqueaderoSIM> parqueaderos = new ArrayList();
        
        try{
          //Register JDBC driver
          Class.forName(JDBC_DRIVER);

          //Open a connection
          System.out.println("Conectando a la base de datos...");
          conn = DriverManager.getConnection(DB_URL_SIM, USER, PASS);
          System.out.println("Base de datos conectada...");

          //Execute a query
          System.out.println("Obteniendo parqueaderos...");
          stmt = conn.createStatement();

          String sql = "SELECT * FROM Parqueaderos";
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while(rs.next()){
                    //Retrieve by column name
                    int id  = rs.getInt("idParqueadero");
                    double lat = rs.getDouble("latitud");
                    double lon = rs.getDouble("longitud");
                    int valor = rs.getInt("valor");
                    int ofertas = rs.getInt("ofertas");
                    int servicios = rs.getInt("servicios");
                    int llaves = rs.getInt("llaves");
                    //Save values
                    p_sim = new ParqueaderoSIM(id, lat, lon, valor, ofertas, servicios, llaves);
                    parqueaderos.add(p_sim);
                } 
            }
            conn.close();
       }catch(SQLException | ClassNotFoundException se){
          //Handle errors for JDBC
          System.err.println("Error! "); 
          System.err.println(se.getMessage());
       }
        //Handle errors for Class.forName
        finally{
          //finally block used to close resources
          try{
             if(stmt!=null)
                conn.close();
          }catch(SQLException se){
          }// do nothing
          try{
             if(conn!=null)
                conn.close();
          }catch(SQLException se){
             se.printStackTrace();
          }//end finally try
       }//end try
        
       return parqueaderos;
    }
    
    
    public ArrayList obtenerParqueaderosFiltradosLatLonBD(double latitud, double longitud, double distancia, int cantidadParqueaderos){
    
        ParqueaderoSIM p_sim;
        ArrayList<ParqueaderoSIM> parqueaderos = new ArrayList();
        System.out.println("La distancia es: "+distancia);
        System.out.println("La cantidad es: "+cantidadParqueaderos);
        try{
          //Register JDBC driver
          Class.forName(JDBC_DRIVER);

          //Open a connection
          System.out.println("Conectando a la base de datos...");
          conn = DriverManager.getConnection(DB_URL_SIM, USER, PASS);
          System.out.println("Base de datos conectada...");

          //Execute a query
          System.out.println("Obteniendo parqueaderos...");
          stmt = conn.createStatement();

          String sql = "SELECT *, ( 6371 * acos( cos( radians("+latitud+") ) * cos( radians( latitud ) ) * cos( radians( longitud ) - radians("+longitud+")) + sin(radians("+latitud+")) * sin( radians( latitud ) ) ) ) AS distancia FROM Parqueaderos WHERE idParqueadero <= "+cantidadParqueaderos+" HAVING distancia <= "+distancia+";";
          //String sql = "SELECT *, ( 6371 * acos( cos( radians("+latitud+") ) * cos( radians( latitud ) ) * cos( radians( longitud ) - radians("+longitud+")) + sin(radians("+latitud+")) * sin( radians( latitud ) ) ) ) AS distancia FROM Parqueaderos HAVING distancia <= "+distancia+";";  
          try (ResultSet rs = stmt.executeQuery(sql)) {
                while(rs.next()){
                    //Retrieve by column name
                    int id  = rs.getInt("idParqueadero");
                    double lat = rs.getDouble("latitud");
                    double lon = rs.getDouble("longitud");
                    int valor = rs.getInt("valor");
                    int ofertas = rs.getInt("ofertas");
                    int servicios = rs.getInt("servicios");
                    int llaves = rs.getInt("llaves");
                    //Save values
                    p_sim = new ParqueaderoSIM(id, lat, lon, valor, ofertas, servicios, llaves);
                    parqueaderos.add(p_sim);
                } 
            }
       }catch(SQLException | ClassNotFoundException se){
          //Handle errors for JDBC
          System.err.println("Error! "); 
          System.err.println(se.getMessage());
       }
        //Handle errors for Class.forName
        finally{
          //finally block used to close resources
          try{
             if(stmt!=null)
                conn.close();
          }catch(SQLException se){
          }// do nothing
          try{
             if(conn!=null)
                conn.close();
          }catch(SQLException se){
             se.printStackTrace();
          }//end finally try
       }//end try
        
        return parqueaderos;
    }
    
    public int obtenerCantidadDeParqueaderos(){
        
        int cantidad = 0;
        
      try{
            //Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //Open a connection
            System.out.println("Conectando a la base de datos...");
            conn = DriverManager.getConnection(DB_URL_SIM, USER, PASS);
            System.out.println("Base de datos conectada...");

            //Execute a query
            System.out.println("Obteniendo parqueaderos...");
            stmt = conn.createStatement();

            String sql = "SELECT COUNT(*) FROM Parqueaderos";
        
            try (ResultSet rs = stmt.executeQuery(sql)) {
              rs.next();
              cantidad = rs.getInt(1);
              conn.close();
            }
       }catch(SQLException | ClassNotFoundException se){
          //Handle errors for JDBC
          System.err.println("Error! "); 
          System.err.println(se.getMessage());
       }
        //Handle errors for Class.forName
        finally{
          //finally block used to close resources
          try{
             if(stmt!=null)
                conn.close();
          }catch(SQLException se){
          }// do nothing
          try{
             if(conn!=null)
                conn.close();
          }catch(SQLException se){
             se.printStackTrace();
          }//end finally try
       }//end try
        
        return cantidad;
    }
}
