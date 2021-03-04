package repository;

import model.Bruker;
import model.Ordre;
import model.Parkeringsplass;

import java.util.ArrayList;
import java.util.List;

public interface IRepository {
     ArrayList<Parkeringsplass> getParkeringsplasser(String sqlwhere);

     void leggTilParkeringsplass(Parkeringsplass plass);

     void fjernParkeringsplass(String id);

     Bruker hentBruker(String id);

     List<Bruker> hentAlleBrukere();

     void leggTilBruker(Bruker bruker);

     ArrayList<Ordre> hentOrdreFraBruker(String brukerId);

     List<Ordre> hentAlleOrdre();

     void leggTilOrdre(Ordre ordre);

     void fjernOrdre(String id);

     ArrayList<String> getRegistreringsnumre(String brukerid);

     void leggTilRegistreringsnummer(String nummer, String brukerid);

     void fjernRegNr(String regnrSlett);

     long hentKortnummer(String brukerId);

     void leggTilKortnummer(String id, long nummer);

     void fjernFraDatabase(String sqlsetning);

     void formaterAntallPlasserParkeringsplass(Parkeringsplass plass);
}
