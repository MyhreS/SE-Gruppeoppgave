package controller;

import fakeExternal.FakeBetalingstjeneste;
import io.javalin.http.Context;
import model.Bruker;
import model.Ordre;
import model.Parkeringsplass;
import repository.DatabaseRepository;
import repository.IRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static controller.ControllerHelper.*;

public class DatabaseController {
    private IRepository repo;
    @SuppressWarnings("rawtypes")
    private List<List> listIndex = new ArrayList<>();
    private Bruker bruker = null;
    private List<Parkeringsplass> parkeringsplasser;
    private List<Bruker> brukere;

    public DatabaseController(DatabaseRepository repo) {
        this.repo = repo;
        parkeringsplasser = repo.getParkeringsplasser("");
        formaterParkeringsplasserAntall(parkeringsplasser);
        brukere = repo.hentAlleBrukere();
        //siden denne er en liste med lister, må jeg lage en liste å ha brukeren i
        ArrayList<Bruker> listeMedBrukeren = new ArrayList<>();
        listeMedBrukeren.add(bruker);
        listIndex.add(listeMedBrukeren);
        listIndex.add(parkeringsplasser);
        listIndex.add(brukere);
    }

    public void jsonListIndex(Context ctx) {
        ctx.json(listIndex);
        ctx.status(200);
    }

    public void jsonBruker(Context ctx) {
        ctx.json(bruker);
        ctx.status(200);
    }

    public Parkeringsplass opprettParkeringsplass(Context ctx) {
        Parkeringsplass parkeringsplass = new Parkeringsplass();
        ArrayList<String> parametre = new ArrayList<>(Arrays.asList(
                "leiUt.lengdegrad", "leiUt.breddegrad", "leiUt.pris", "leiUt.antall", "leiUt.antallHandicap",
                "leiUt.fra", "leiUt.til"));
        int nullNummer = erFormParamNull(parametre, ctx);
        if(nullNummer != -1){
            System.err.println("Verdier mangler i ctx.formParam(): " + parametre.get(nullNummer));
            ctx.status(400);
            return null;
        }

        else if(erIkkeDouble(ctx.formParam("leiUt.lengdegrad")) || erIkkeDouble(ctx.formParam("leiUt.breddegrad")) ||
                erIkkeDouble(ctx.formParam("leiUt.pris")) || erIkkeInteger(ctx.formParam("leiUt.antall")) ||
                erIkkeInteger(ctx.formParam("leiUt.antallHandicap"))){
            System.err.println("Verdiene er ikke ordentlige!");
            System.err.println(
                    ctx.formParam("leiUt.lengdegrad")+"\n"+
                    ctx.formParam("leiUt.breddegrad")+"\n"+
                    ctx.formParam("leiUt.pris")+"\n"+
                    ctx.formParam("leiUt.antall")+"\n"+
                    ctx.formParam("leiUt.antallHandicap")+"\n"
            );

            ctx.status(400);
            return null;
        }

        try {
            double lengdegrad = Double.parseDouble(ctx.formParam("leiUt.lengdegrad"));
            double breddegrad = Double.parseDouble(ctx.formParam("leiUt.breddegrad"));
            int antallPlasser = Integer.parseInt(ctx.formParam("leiUt.antall"));
            int antallHandicap = Integer.parseInt(ctx.formParam("leiUt.antallHandicap"));
            double pris = Double.parseDouble(ctx.formParam("leiUt.pris"));
            String beskrivelse = ctx.formParam("leiUt.beskrivelse");
            String eierId = ctx.formParam("leiUt.brukerId");
            LocalDateTime til = LocalDateTime.parse(ctx.formParam("leiUt.fra"));
            LocalDateTime fra = LocalDateTime.parse(ctx.formParam("leiUt.til"));

            parkeringsplass = new Parkeringsplass(lengdegrad, breddegrad, antallPlasser, antallHandicap, pris, beskrivelse, eierId, til, fra);
            parkeringsplasser.add(parkeringsplass);
            listIndex.set(1, parkeringsplasser);
        } catch(NullPointerException e) {
            e.printStackTrace();
            ctx.status(400);
        }
        ctx.status(200);
        return parkeringsplass;
    }

    public void lagParkeringsplass(Context ctx) {
        Parkeringsplass nyParkeringsplass = opprettParkeringsplass(ctx);
        if(nyParkeringsplass == null){
            return;
        }
        repo.leggTilParkeringsplass(nyParkeringsplass);
    }

    public Ordre opprettOrdre(Context ctx) {
        ArrayList<String> parametre = new ArrayList<>(Arrays.asList(
                "bruker.id", "ledig.parkerinsgplassId", "ledig.fra", "ledig.til"));
        int nullNummer = erFormParamNull(parametre, ctx);
        if(nullNummer != -1){
            System.err.println("Verdier mangler i ctx.formParam(): " + parametre.get(nullNummer));
            ctx.status(400);
            return null;
        }
        Ordre nyOrdre;
        try {
            String eierId = ctx.formParam("bruker.id");
            String parkeringsplassId = ctx.formParam("ledig.parkerinsgplassId");
            boolean handicap = erFormParamNull("ledig.handicap", ctx);
            LocalDateTime til = LocalDateTime.parse(ctx.formParam("ledig.fra"));
            LocalDateTime fra = LocalDateTime.parse(ctx.formParam("ledig.til"));
            nyOrdre = new Ordre(eierId, parkeringsplassId, handicap, til, fra);
            return nyOrdre;
        } catch (NullPointerException e) {
            e.printStackTrace();
            ctx.status(400);
        }
        return null;
    }

    public boolean gyldigBetalingsinfo() {
        if(FakeBetalingstjeneste.sjekkKort(bruker.getKortnummer())) {
            return true;
        }
        System.err.println("Ikke gyldig kortnummer");
        return false;
    }

    public void lagOrdre(Context ctx) {
        Ordre nyOrdre = opprettOrdre(ctx);
        if(!gyldigBetalingsinfo()) {
            return;
        }
        repo.leggTilOrdre(nyOrdre);


        for(int i = 0; i < parkeringsplasser.size(); i++) {
            if(nyOrdre.getParkerinsgplassId().equals(parkeringsplasser.get(i).getId())) {
                parkeringsplasser.get(i).antallMinus(nyOrdre.isHandicap());
                break;
            }
        }
        ctx.status(201);
    }

    public void settOppBrukerinfo(Context ctx) {
        String brukerId = ctx.formParam("redirectBrukerId");
        bruker.setRegistreringsnummer(repo.getRegistreringsnumre(brukerId));
        bruker.setLeierInn(repo.hentOrdreFraBruker(brukerId));
        bruker.setLeierUt(repo.getParkeringsplasser("WHERE eierId LIKE '" + brukerId + "'"));
        bruker.setKortnummer(repo.hentKortnummer(brukerId));
        ctx.status(200);
    }

    public void loggInn(Context ctx) {
        bruker = repo.hentBruker(ctx.formParam("b.id"));
        bruker.setKortnummer(repo.hentKortnummer(bruker.getId()));
        listIndex.get(0).set(0, bruker);
        ctx.json(listIndex);
    }

    public void redigerKortnr(Context ctx) {
        if(!ctx.formParam("bruker.kortnummer").equals("")) {
            if(erIkkeLong(ctx.formParam("bruker.kortnummer"))){
                System.err.println("Feil i kortnummer: " + ctx.formParam("bruker.kortnummer"));
            } else {
                long kortnr = Long.parseLong(ctx.formParam("bruker.kortnummer"));
                bruker.setKortnummer(kortnr);
                repo.leggTilKortnummer(bruker.getId(), kortnr);
            }
        }
    }

    public void leggTilRegnr(Context ctx) {
        String regnr = ctx.formParam("leggTilRegnr").toUpperCase();
        ArrayList<String> regnrList = bruker.getRegistreringsnummer();
        boolean contain = false;
        for (String nr : regnrList) {
            if (nr.equals(regnr)) {
                contain = true;
                break;
            }
        }
        if(!contain && regnr.length() <= 12) {
            bruker.leggTilRegNr(regnr);
            repo.leggTilRegistreringsnummer(regnr, bruker.getId());
        }
    }

    public void slettRegnr(Context ctx) {
        String regnrSlett = ctx.formParam("slett");
        ArrayList<String> regnrList = bruker.getRegistreringsnummer();
        for(int i = 0; i < regnrList.size(); i++) {
            if(regnrList.get(i).equals(regnrSlett)) {
                bruker.slettRegNr(regnrSlett);
                repo.fjernRegNr(regnrSlett);
            }
        }
    }

    public void slettOrdre(Context ctx) {
        String id = ctx.formParam("ordreId");
        List<Ordre> ordreList = bruker.getLeierInn();
        for(int i = 0; i < ordreList.size(); i++) {
            for(int j = 0; j < parkeringsplasser.size(); j++) {
                if(ordreList.get(i).getParkerinsgplassId().equals(parkeringsplasser.get(j).getId())) {
                    parkeringsplasser.get(j).antallPluss(ordreList.get(i).isHandicap());
                }
            }
            if(ordreList.get(i).getId().equals(id)) {
                bruker.fjernOrdre(id);
                repo.fjernOrdre(id);
            }
        }
        listIndex.set(1, parkeringsplasser);
    }

    public void slettParkeringsplass(Context ctx) {
        List<Parkeringsplass> plassList;
        String parkid = ctx.formParam("parkeringsplassId");
        if(bruker.isAdmin()) {
            plassList = parkeringsplasser;
        } else {
            plassList = bruker.getLeierUt();
        }
        //Finner først parkeringsplassen i brukerens tilgjengelige liste, og så finner den i parkeringsplasslisten og sletter den der og i repo
        for (Parkeringsplass parkeringsplass : plassList) {
            if (parkeringsplass.getId().equals(parkid)) {
                for (int j = 0; j < parkeringsplasser.size(); j++) {
                    if (parkeringsplasser.get(j).getId().equals(parkeringsplass.getId())) {
                        parkeringsplasser.remove(parkeringsplasser.get(j));
                        break;
                    }
                }
                listIndex.set(1, parkeringsplasser);
                repo.fjernParkeringsplass(parkid);
                bruker.slettParkeringsplass(parkid);
                break;
            }
        }
    }

    public void formaterParkeringsplasserAntall(List<Parkeringsplass> plasser){
        //utenfor prototypen blir dette sannsynligvis lagret i databasen inni en egen tabell
        for(Parkeringsplass plass : plasser){
            repo.formaterAntallPlasserParkeringsplass(plass);
        }
    }

    public IRepository getRepo() {
        return repo;
    }

    public void setRepo(IRepository repo) {
        this.repo = repo;
    }

    public List<List> getList() {
        return listIndex;
    }

    public void setList(List<List> list) {
        this.listIndex = list;
    }

    public List<Parkeringsplass> getParkeringsplasser() {
        return parkeringsplasser;
    }

    public void setParkeringsplasser(List<Parkeringsplass> parkeringsplasser) {
        this.parkeringsplasser = parkeringsplasser;
    }

    public List<Bruker> getBrukere() {
        return brukere;
    }

    public void setBrukere(List<Bruker> brukere) {
        this.brukere = brukere;
    }

    public List<List> getListIndex() {
        return listIndex;
    }

    public void setListIndex(List<List> listIndex) {
        this.listIndex = listIndex;
    }

    public Bruker getBruker() {
        return bruker;
    }

    public void setBruker(Bruker bruker) {
        this.bruker = bruker;
    }

}
