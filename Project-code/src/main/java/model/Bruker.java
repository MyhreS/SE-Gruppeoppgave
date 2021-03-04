package model;

import java.util.ArrayList;
import java.util.UUID;

public class Bruker {
    private String navn;
    private String id;
    private ArrayList<String> registreringsnummer = new ArrayList<>();
    private ArrayList<Ordre> leierInn;
    private ArrayList<Parkeringsplass> leierUt = new ArrayList<>();
    private long kortnummer = 0000_0000_0000_0000; //bare lat som at denne blir hashet i databasen
    protected boolean adminStatus = false;

    public Bruker(String navn, String id) {
        this.navn = navn;
        this.id = id;
    }

    public Bruker(String navn) {
        this.navn = navn;
        this.id = UUID.randomUUID().toString();
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id){this.id = id;}

    public ArrayList<String> getRegistreringsnummer() {
        return registreringsnummer;
    }

    public void setRegistreringsnummer(ArrayList<String> registreringsnummer) {
        this.registreringsnummer = registreringsnummer;
    }

    public ArrayList<Ordre> getLeierInn() {
        return leierInn;
    }

    public void setLeierInn(ArrayList<Ordre> leierInn) {
        this.leierInn = leierInn;
    }

    public ArrayList<Parkeringsplass> getLeierUt() {
        return leierUt;
    }

    public void setLeierUt(ArrayList<Parkeringsplass> leierUt) {
        this.leierUt = leierUt;
    }

    public void leggTilRegNr(String regNr){
        registreringsnummer.add(regNr);
    }

    public void slettRegNr(String regNr) {
        registreringsnummer.remove(regNr);
    }

    public void slettParkeringsplass(String id) {
        for(int i = 0; i < leierUt.size(); i++) {
            if(leierUt.get(i).getId().equals(id)) {
                leierUt.remove(leierUt.get(i));
            }
        }
    }

    public void fjernOrdre(String id) {
        for(int i = 0; i < leierInn.size(); i++) {
            if(leierInn.get(i).getId().equals(id)) {
                leierInn.remove(leierInn.get(i));
            }
        }
    }

    public void leierUtParkeringsplass(Parkeringsplass parkeringsplass) {
        leierUt.add(parkeringsplass);
    }

    public void leierInnParkeringsplass(Ordre ordre) {
        leierInn.add(ordre);
    }

    public long getKortnummer() {
        return kortnummer;
    }

    public void setKortnummer(long kortnummer) {
        this.kortnummer = kortnummer;
    }

    public boolean isAdmin() {
        return adminStatus;
    }

    @Override
    public String toString() {
        return "Bruker{" +
                "navn='" + navn + '\'' +
                ", id='" + id + '\'' +
                ", registreringsnummer=" + registreringsnummer +
                ", leierInn=" + leierInn +
                ", leierUt=" + leierUt +
                ", kortnummer=" + kortnummer +
                '}';
    }


}
