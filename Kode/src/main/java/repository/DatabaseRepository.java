package repository;

import model.Administrator;
import model.Bruker;
import model.Ordre;
import model.Parkeringsplass;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class DatabaseRepository implements IRepository{
    Statement statement;
    Connection connection;
    ResultSet rs;
    String path = "jdbc:sqlite:src/main/resources/database.db";

    public ArrayList<Parkeringsplass> getParkeringsplasser(String sqlWhere){
        ArrayList<Parkeringsplass> plasser = new ArrayList<>();
        try {
            establishConnection();
            if(sqlWhere.equals(""))
                rs = statement.executeQuery("select * from parkeringsplasser");
            else
                rs = statement.executeQuery("select * from parkeringsplasser " + sqlWhere);
            //f.eks. WHERE eierId = sdfjklsjfjalkjfak
            while(rs.next())
            {
                // read the result set
                Parkeringsplass plass = new Parkeringsplass(rs.getString("id"), rs.getDouble("lengdegrad"),
                        rs.getDouble("breddegrad"), rs.getInt("antallPlasser"),
                        rs.getInt("antallHandicap"), rs.getDouble("pris"),
                        rs.getString("beskrivelse"), rs.getString("eierId"),
                        konverterFraSQLtilJavaTid(rs.getInt("fra")), konverterFraSQLtilJavaTid(rs.getInt("til")));
                plasser.add(plass);
            }
        }
        catch(SQLException e){
            System.err.println(e.getMessage());
        }
        disconnectConnection();
        return plasser;
    }

    public void leggTilParkeringsplass(Parkeringsplass plass){
        try {
            establishConnection();
            statement.executeUpdate("insert into parkeringsplasser values('"+
                    plass.getId()+"',"+plass.getLengdegrad()+","+plass.getBreddegrad()+","+plass.getAntallPlasser()+","+
                    plass.getAntallHandicap()+","+ plass.getPris()+",'"+plass.getBeskrivelse()+"','"+plass.getEierId()+"',"+
                    konverterFraJavatilSQLTid(plass.getFra()) + "," +konverterFraJavatilSQLTid(plass.getTil())+
                    ");");
        }
        catch(SQLException e) {
            System.err.println(e.getMessage());
        }
        disconnectConnection();
    }

    public void formaterAntallPlasserParkeringsplass(Parkeringsplass plass){
        establishConnection();
        try{
            rs = statement.executeQuery(
                    "SELECT handicap FROM ordre WHERE parkeringsplassId LIKE '" + plass.getId() + "';");
            while(rs.next()){
                if(rs.getBoolean("handicap")){
                    plass.setAntallHandicap(plass.getAntallHandicap() - 1);
                }
                else{
                    plass.setAntallPlasser(plass.getAntallPlasser() - 1);
                }
            }
        }
        catch(SQLException e){ System.err.println(e.getMessage()); }
        disconnectConnection();
    }

    public ArrayList<Ordre> hentOrdreFraBruker(String brukerId){
        ArrayList<Ordre> ordreListe = new ArrayList<>();
        try {
            establishConnection();
            rs = statement.executeQuery("select * from ordre WHERE personId = '" + brukerId + "'");
            while(rs.next())
            {
                // read the result set
                Ordre ordre = new Ordre(rs.getString("id"),
                        rs.getString("personId"),
                        rs.getString("parkeringsplassId"),
                        rs.getBoolean("handicap"),
                        konverterFraSQLtilJavaTid(rs.getLong("fra")),
                        konverterFraSQLtilJavaTid(rs.getLong("til")));
                ordreListe.add(ordre);
            }

            for(Ordre o : ordreListe){
                rs = statement.executeQuery("SELECT navn FROM personer " +
                        "WHERE personer.id IN (SELECT parkeringsplasser.eierId FROM ordre, parkeringsplasser " +
                        "WHERE parkeringsplasser.id = '" + o.getParkerinsgplassId() + "');");
                while(rs.next()){
                    o.setEiernavn(rs.getString("navn"));
                }
            }
        }
        catch(SQLException e){ System.err.println(e.getMessage()); }
        disconnectConnection();
        return ordreListe;
    }

    public ArrayList<Bruker> hentAlleBrukere(){
        ArrayList<Bruker> brukere = new ArrayList<>();
        try {
            establishConnection();
            rs = statement.executeQuery("select * from personer");
            while(rs.next()) {
                // read the result set
                if(rs.getInt("erAdmin") == 1){
                    Administrator admin = new Administrator(rs.getString("navn"), rs.getString("id"));
                    brukere.add(admin);
                }
                else{
                    Bruker bruker = new Bruker(rs.getString("navn"), rs.getString("id"));
                    brukere.add(bruker);
                }
            }
        }
        catch(SQLException e){ System.err.println(e.getMessage()); }
        disconnectConnection();
        return brukere;
    }

    public Bruker hentBruker(String id){
        Bruker bruker = new Bruker("temp", "temp");
        try{
            establishConnection();
            rs = statement.executeQuery("select * from personer where id like '" + id + "'");
            while(rs.next()){
                if(rs.getInt("erAdmin") == 1){
                    bruker = new Administrator(rs.getString("navn"), rs.getString("id"));
                }
                else{
                    bruker.setNavn(rs.getString("navn"));
                    bruker.setId(rs.getString("id"));
                }
            }
        }
        catch(SQLException e){ System.err.println(e.getMessage()); }
        disconnectConnection();
        return bruker;
    }

    public List<Ordre> hentAlleOrdre() {
        ArrayList<Ordre> ordre = new ArrayList<>();
        String sporring = "SELECT id, personId, parkeringsplassId, handicap, fra, til FROM ordre";
        try {
            establishConnection();
            rs = statement.executeQuery(sporring);
            while(rs.next()) {
                String id = rs.getString(1);
                String personId = rs.getString(2);
                String parkeringsplassId = rs.getString(3);
                boolean handicap = rs.getBoolean(4);
                LocalDateTime fra = konverterFraSQLtilJavaTid(rs.getLong(5));
                LocalDateTime til = konverterFraSQLtilJavaTid(rs.getLong(6));

                Ordre enOrdre = new Ordre(id, personId, parkeringsplassId, handicap, fra, til);
                ordre.add(enOrdre);
            }
        } catch(SQLException e){ System.err.println(e.getMessage()); }
        disconnectConnection();
        return ordre;
    }

    public Ordre hentOrdre(String id){
        Ordre ordrefraDB = null;
        try{
            establishConnection();
            rs = statement.executeQuery("SELECT * FROM ordre WHERE id LIKE '" + id + "';");
            while(rs.next()){
                ordrefraDB = new Ordre(rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getBoolean(4), konverterFraSQLtilJavaTid(rs.getLong(5)),
                        konverterFraSQLtilJavaTid(rs.getLong(6)));
            }
        }
        catch(SQLException e){ System.err.println(e.getMessage()); }
        disconnectConnection();
        return ordrefraDB;
    }

    public void leggTilOrdre(Ordre ordre){
        try {
            establishConnection();
            statement.executeUpdate("insert into ordre values("+
                    "'"+ordre.getId()+"','"+ordre.getPersonid()+"','"+ordre.getParkerinsgplassId()+"',"+
                    ordre.isHandicap()+","+konverterFraJavatilSQLTid(ordre.getFra())+","+
                    konverterFraJavatilSQLTid(ordre.getTil())+
                    ")");
        }
        catch(SQLException e){ System.err.println(e.getMessage()); }
        disconnectConnection();
    }

    public void leggTilBruker(Bruker bruker){
        try {
            establishConnection();
            statement.executeUpdate("insert into personer values('"+
                    bruker.getId()+"','"+bruker.getNavn()+"','" + bruker.getKortnummer() + "', " + booleanTilInteger(bruker.isAdmin()) +
                    ")");
        }
        catch(SQLException e){ System.err.println(e.getMessage()); }
        disconnectConnection();
    }

    public ArrayList<String> getRegistreringsnumre(String brukerid) {
        ArrayList<String> regNumre = new ArrayList<>();
        try {
            establishConnection();
            rs = statement.executeQuery("SELECT * FROM registreringsnumre WHERE personId LIKE '" + brukerid + "'");
            while(rs.next()){
                regNumre.add(rs.getString("registreringsnummer"));
            }

        }
        catch(SQLException e){ System.err.println(e.getMessage()); }
        disconnectConnection();

        return regNumre;
    }

    public long hentKortnummer(String id){
        long nummer = 0;
        establishConnection();
        try{
            rs = statement.executeQuery("Select kortnummer from personer where id like '" + id + "'");
            while(rs.next()){
                nummer = rs.getLong("kortnummer");
            }
        }
        catch(SQLException e){ System.err.println(e.getMessage()); }
        disconnectConnection();
        return nummer;
    }

    public void fjernOrdre(String id) {
        fjernFraDatabase("DELETE FROM ordre WHERE id LIKE '" + id + "';");
    }

    public void fjernRegNr(String regnrSlett) {
        fjernFraDatabase("DELETE FROM registreringsnumre WHERE registreringsnummer = '" + regnrSlett + "'");
    }

    public void fjernParkeringsplass(String id) {
        fjernFraDatabase("DELETE FROM parkeringsplasser WHERE id = '" + id + "'");
    }

    public void leggTilKortnummer(String id, long nummer){
        establishConnection();
        try{
            statement.executeUpdate("UPDATE personer SET kortnummer = " + nummer + " WHERE id LIKE '" + id + "'");
        }
        catch(SQLException e){ System.err.println(e.getMessage()); }
        disconnectConnection();
    }

    public void leggTilRegistreringsnummer(String nummer, String brukerid) {
        try {
            establishConnection();
            statement.executeUpdate("INSERT INTO registreringsnumre VALUES('"+
                    brukerid + "','"+ nummer +
                    "')");
        }
        catch(SQLException e){ System.err.println(e.getMessage()); }
        disconnectConnection();
    }

    public void fjernFraDatabase(String sqlsetning){
        try {
            establishConnection();
            statement.executeUpdate(sqlsetning);
        }
        catch (SQLException e){
            System.err.print(e.getMessage());
        }
        disconnectConnection();
    }

    public long konverterFraJavatilSQLTid(LocalDateTime tid){
        return tid.toEpochSecond(ZoneOffset.UTC);
    }

    public LocalDateTime konverterFraSQLtilJavaTid(long SQLtid){
        return LocalDateTime.ofEpochSecond(SQLtid, 0, ZoneOffset.UTC);
    }

    public int booleanTilInteger(boolean bool){
        if(bool)
            return 1;
        return 0;
    }

    private void establishConnection(){
        try{
            connection = DriverManager.getConnection(path);
            statement = connection.createStatement();
            statement.setQueryTimeout(30);
        }
        catch (SQLException e){
            //om den ikke får koplet seg på databasen

            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.print(e.getMessage());
        }
    }

    private void disconnectConnection(){
        try {
            if(connection != null)
                connection.close();
        }
        catch(SQLException e) {
            // connection close failed.
            System.err.println(e.getMessage());
        }
    }
}