package repository;

import controller.DatabaseController;
import io.javalin.http.Context;
import io.javalin.http.util.JsonEscapeUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.*;

import model.Bruker;
import model.Ordre;
import model.Parkeringsplass;
import repository.DatabaseRepository;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Repository {
    DatabaseRepository repository = new DatabaseRepository();
    String id = UUID.randomUUID().toString(); //Id blir brukt til objekter i testene

    final ByteArrayOutputStream err = new ByteArrayOutputStream();
    final PrintStream originalErr = System.err;

    @BeforeEach
    public void setUpStreams() {
        System.setErr(new PrintStream(err));
    }

    @AfterEach
    public void restoreStreams() {
        System.setErr(originalErr);
    }

    @Test
    public void fjerner_fra_DB_error() {
        repository.fjernFraDatabase("DELETE FROM personer WHERE id LIKE INDEX");
        assertTrue(err.toString().contains("[SQLITE_ERROR]"));
    }

    @Nested
    class DB_parkeringsplasser {
        LocalDateTime til = LocalDateTime.now().withNano(0);
        LocalDateTime fra = LocalDateTime.now().withNano(0);
        Parkeringsplass plass = new Parkeringsplass(id, 1, 1, 1, 1,
                1, "Test parkeringsplass", "TestId", til, fra);

        @AfterEach
        public void fjernParkeringsplass(){
            repository.fjernFraDatabase("DELETE FROM parkeringsplasser WHERE id LIKE '"+id+"'");
        }

        @Test
        public void kan_hente_parkeringsplass_liste(){

            ArrayList<Parkeringsplass> parkPlasserIListe = new ArrayList<>();
            parkPlasserIListe.add(plass);
            repository.leggTilParkeringsplass(plass);
            ArrayList<Parkeringsplass> parkPlassFraDB = new ArrayList<>(repository.getParkeringsplasser("WHERE id LIKE '"+id+"'"));

            assertEquals(parkPlasserIListe.toString(), parkPlassFraDB.toString());
        }

        @Test
        public void kan_legge_til_Parkeringsplass() {
            ArrayList<Parkeringsplass> parkeringslasserIListe = new ArrayList<>();
            parkeringslasserIListe.add(plass);
            repository.leggTilParkeringsplass(plass);

            assertEquals(repository.getParkeringsplasser("WHERE id LIKE '"+id+"'").toString(), parkeringslasserIListe.toString());
        }

        @Test
        public void kan_fjerne_parkeringsplass(){
            repository.leggTilParkeringsplass(plass);
            repository.fjernParkeringsplass(id);
            ArrayList<Parkeringsplass> plasser = repository.getParkeringsplasser("WHERE id LIKE '" + id + "';");

            assertEquals(0, plasser.size());
        }
    }

    @Nested
    class DB_ordre {
        LocalDateTime til = LocalDateTime.now().withNano(0);
        LocalDateTime fra = LocalDateTime.now().withNano(0);
        Ordre ordre = new Ordre(id, id, id, true, til, fra);

        @AfterEach
        public void fjernOrdre(){
            repository.fjernFraDatabase("DELETE FROM ordre WHERE id LIKE '"+id+"'");
        }

        @Test
        public void kan_hente_ordre_fra_bruker() {
            ArrayList<Ordre> actual = new ArrayList<>(repository.hentOrdreFraBruker(id));
            repository.leggTilOrdre(ordre);
            actual.add(ordre);
            ArrayList<Ordre> expected = new ArrayList<>(repository.hentOrdreFraBruker(id));
            assertEquals(expected.toString(), actual.toString());
        }

        @Test
        public void kan_legge_til_ordre() {
            List<Ordre> actual = new ArrayList<>(repository.hentAlleOrdre());
            repository.leggTilOrdre(ordre);
            actual.add(ordre);
            List<Ordre> expected = new ArrayList<>(repository.hentAlleOrdre());
            assertEquals(expected.toString(), actual.toString());
        }

        @Test
        public void kan_fjerne_ordre_fra_database(){
            repository.leggTilOrdre(ordre);
            repository.fjernOrdre(id);
            Ordre ordreFraDB = repository.hentOrdre(id);
            assertNull(ordreFraDB);
        }
    }

    @Nested
    class DB_bruker {
        Bruker bruker = new Bruker("Testus Testesen", id);

        @AfterEach
        public void fjernBruker(){
            repository.fjernFraDatabase("DELETE FROM personer WHERE id LIKE '"+id+"'");
        }

        @Test
        public void kan_legge_til_bruker() {
            ArrayList<Bruker> listeMedAlleBrukerene = new ArrayList<>(repository.hentAlleBrukere());
            repository.leggTilBruker(bruker);
            listeMedAlleBrukerene.add(bruker);
            ArrayList<Bruker> listeMedAlleBrukereneEtterOperasjon = new ArrayList<>(repository.hentAlleBrukere());

            assertEquals(listeMedAlleBrukerene.toString(), listeMedAlleBrukereneEtterOperasjon.toString());
        }

        @Test
        public void kan_fjerne_bruker() {
            ArrayList<Bruker> listeMedAlleBrukerene = new ArrayList<>(repository.hentAlleBrukere());
            repository.leggTilBruker(bruker);
            repository.fjernFraDatabase("DELETE FROM personer WHERE id LIKE '"+id+"'");
            ArrayList<Bruker> listeMedAlleBrukereneEtterOperasjon = new ArrayList<>(repository.hentAlleBrukere());

            assertEquals(listeMedAlleBrukerene.toString(), listeMedAlleBrukereneEtterOperasjon.toString());
        }

        @Test
        public void kan_hente_alle_brukere() {
            ArrayList<Bruker> brukereActual = repository.hentAlleBrukere();
            repository.leggTilBruker(bruker);
            brukereActual.add(bruker);
            ArrayList<Bruker> brukereExpected = repository.hentAlleBrukere();

            assertEquals(brukereActual.toString(), brukereExpected.toString());
        }

        @Test
        public void kan_hente_en_bruker() {
            repository.leggTilBruker(bruker);
            boolean actual = false;

            Bruker bruker2 = repository.hentBruker(bruker.getId());
            assertEquals(bruker.toString(), bruker2.toString());
        }

    }

    @Nested
    class DB_registreringsnummer{
        String regNummer = "YT59274";

        @AfterEach
        public void fjernRegistreringsnummer(){
            repository.fjernFraDatabase("DELETE FROM registreringsnumre WHERE personId LIKE '"+id+"'");
        }

        @Test
        public void kan_legge_til_regnr() {
            repository.leggTilRegistreringsnummer(regNummer, id);
            ArrayList<String> numre = repository.getRegistreringsnumre(id);
            assertEquals(regNummer, numre.get(0));
        }

        @Test
        public void kan_fjerne_regnr(){
            repository.leggTilRegistreringsnummer(regNummer, id);
            repository.fjernRegNr(regNummer);
            ArrayList<String> numre = repository.getRegistreringsnumre(id);
            assertEquals(0, numre.size());
        }
    }
}



