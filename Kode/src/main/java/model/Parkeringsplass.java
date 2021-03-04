package model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Parkeringsplass {
    private String id;
    private double lengdegrad;
    private double breddegrad;
    private int antallPlasser;
    private int antallHandicap;
    private double pris; //per time utleid
    private String beskrivelse;
    private String eierId;
    private LocalDateTime fra;
    private LocalDateTime til;

    public Parkeringsplass() {
    }

    public Parkeringsplass(String id, double lengdegrad, double breddegrad, int antallPlasser, int antallHandicap, double pris, String beskrivelse, String eierId, LocalDateTime fra, LocalDateTime til) {
        this.id = id;
        this.lengdegrad = lengdegrad;
        this.breddegrad = breddegrad;
        this.antallPlasser = antallPlasser;
        this.antallHandicap = antallHandicap;
        this.pris = pris;
        this.beskrivelse = beskrivelse;
        this.eierId = eierId;
        this.til = til;
        this.fra = fra;
    }

    public Parkeringsplass(double lengdegrad, double breddegrad, int antallPlasser, int antallHandicap, double pris, String beskrivelse, String eierId, LocalDateTime fra, LocalDateTime til) {
        this.id = UUID.randomUUID().toString();
        this.lengdegrad = lengdegrad;
        this.breddegrad = breddegrad;
        this.antallPlasser = antallPlasser;
        this.antallHandicap = antallHandicap;
        this.pris = pris;
        this.beskrivelse = beskrivelse;
        this.eierId = eierId;
        this.fra = fra;
        this.til = til;
    }

    public void antallPluss(boolean handicap) {
        if(!handicap) {
            antallPlasser++;
        } else {
            antallHandicap++;
        }
    }

    public void antallMinus(boolean handicap) {
        if(!handicap) {
            antallPlasser--;
        } else {
            antallHandicap--;
        }
    }

    public String getId(){
        return id;
    }

    public LocalDateTime getFra() {
        return fra;
    }

    public void setFra(LocalDateTime fra) {
        this.fra = fra;
    }

    public LocalDateTime getTil() {
        return til;
    }

    public void setTil(LocalDateTime til) {
        this.til = til;
    }

    public double getLengdegrad() {
        return lengdegrad;
    }

    public void setLengdegrad(double lengdegrad) {
        this.lengdegrad = lengdegrad;
    }

    public double getBreddegrad() {
        return breddegrad;
    }

    public void setBreddegrad(double breddegrad) {
        this.breddegrad = breddegrad;
    }

    public int getAntallPlasser() {
        return antallPlasser;
    }

    public void setAntallPlasser(int antallPlasser) {
        this.antallPlasser = antallPlasser;
    }

    public int getAntallHandicap() {
        return antallHandicap;
    }

    public void setAntallHandicap(int antallHandicap) {
        this.antallHandicap = antallHandicap;
    }

    public double getPris() {
        return pris;
    }

    public void setPris(double pris) {
        this.pris = pris;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    public String getEierId() {
        return eierId;
    }

    public void setEierId(String eierId) {
        this.eierId = eierId;
    }


    @Override
    public String toString() {
        return "Parkeringsplass{" +
                "id='" + id + '\'' +
                ", lengdegrad=" + lengdegrad +
                ", breddegrad=" + breddegrad +
                ", antallPlasser=" + antallPlasser +
                ", antallHandicap=" + antallHandicap +
                ", pris=" + pris +
                ", beskrivelse='" + beskrivelse + '\'' +
                ", eierId='" + eierId + '\'' +
                ", fra=" + fra +
                ", til=" + til +
                '}';
    }
}
