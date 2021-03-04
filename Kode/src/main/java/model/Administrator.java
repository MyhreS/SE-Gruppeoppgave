package model;

public class Administrator extends Bruker {

    public Administrator(String navn) {
        super(navn);
        this.adminStatus = true;
    }

    public Administrator(String navn, String id){
        super(navn, id);
        this.adminStatus = true;
    }
}
