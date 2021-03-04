package model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class Ordre {
    private String id;
    private String personid;
    private String parkerinsgplassId;
    private boolean handicap;
    private LocalDateTime til;
    private LocalDateTime fra;
    private String eiernavn;

    public Ordre(String id, String personid, String parkerinsgplassId, boolean handicap, LocalDateTime til, LocalDateTime fra) {
        this.id = id;
        this.personid = personid;
        this.parkerinsgplassId = parkerinsgplassId;
        this.handicap = handicap;
        this.til = til;
        this.fra = fra;
    }

    public Ordre(String personid, String parkerinsgplassId, boolean handicap, LocalDateTime til, LocalDateTime fra) {
        this.id = UUID.randomUUID().toString();
        this.personid = personid;
        this.parkerinsgplassId = parkerinsgplassId;
        this.handicap = handicap;
        this.til = til;
        this.fra = fra;
    }

    public String getId() {
        return id;
    }

    public String getPersonid() {
        return personid;
    }

    public void setPersonid(String personid) {
        this.personid = personid;
    }

    public String getParkerinsgplassId() {
        return parkerinsgplassId;
    }

    public void setParkerinsgplassId(String parkerinsgplassId) {
        this.parkerinsgplassId = parkerinsgplassId;
    }

    public boolean isHandicap() {
        return handicap;
    }

    public void setHandicap(boolean handicap) {
        this.handicap = handicap;
    }

    public LocalDateTime getTil() {
        return til;
    }

    public void setTil(LocalDateTime til) {
        this.til = til;
    }

    public LocalDateTime getFra() {
        return fra;
    }

    public void setFra(LocalDateTime fra) {
        this.fra = fra;
    }

    public String getEiernavn() {
        return eiernavn;
    }

    public void setEiernavn(String eiernavn) {
        this.eiernavn = eiernavn;
    }

    @Override
    public String toString() {
        return "Ordre{" +
                "id='" + id + '\'' +
                ", personid='" + personid + '\'' +
                ", parkerinsgplassId='" + parkerinsgplassId + '\'' +
                ", handicap=" + handicap +
                ", til=" + til +
                ", fra=" + fra +
                '}';
    }
}
