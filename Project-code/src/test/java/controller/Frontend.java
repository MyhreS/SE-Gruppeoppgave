package controller;

import model.Administrator;
import model.Bruker;
import model.Ordre;
import model.Parkeringsplass;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import repository.DatabaseRepository;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.javalin.http.Context;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class Frontend {
    private Context ctx;
    private DatabaseRepository mockRepository;
    private DatabaseController mockController;
    private DatabaseRepository databaseRepository = new DatabaseRepository();
    private DatabaseController databaseController;
    private String id = UUID.randomUUID().toString();

    final ByteArrayOutputStream err = new ByteArrayOutputStream();
    final PrintStream originalErr = System.err;

    @BeforeEach
    public void before() {
        ctx = mock(Context.class);
        mockRepository = mock(DatabaseRepository.class);
        mockController = new DatabaseController(mockRepository);
        System.setErr(new PrintStream(err));
    }

    @AfterEach
    public void restoreStreams() {
        System.setErr(originalErr);
    }

    @Test
    public void jsonListIndex_gir_200_som_status(){
        //Dette verifiserer at ctx.json blir gjennomført
        mockController.jsonListIndex(ctx);
        verify(ctx).status(200);
    }

    @Test
    public void jsonBruker_gir_200_som_status(){
        mockController.jsonBruker(ctx);
        verify(ctx).status(200);
    }

    @Test
    @DisplayName("Krav 6.1.17")
    public void kan_hente_parkeringsplasser(){
        //kravet er veldig uspesifikt, så "holde styr på" vil jeg anta betyr å ha parkeringsplassene i en variabel
        assertEquals(0, mockController.getParkeringsplasser().size());
        mockController.setParkeringsplasser(databaseRepository.getParkeringsplasser(""));
        assertTrue(mockController.getParkeringsplasser().size() != 0);

        //om getAntallPlasser() returnerer en int, vil denne testen gjennomgås, som vil si at den vet tilgjengelighets-statusen
        assertTrue(mockController.getParkeringsplasser().get(0).getAntallPlasser() < 99999);
    }

    @Test
    @DisplayName("Krav 6.1.12")
    public void opprettParkeringsplass_oppretter_parkeringsplass() {
        LocalDateTime til = LocalDateTime.now().withNano(0);
        LocalDateTime fra = LocalDateTime.now().withNano(0);

        when(ctx.formParam("leiUt.lengdegrad")).thenReturn("59.125832");
        when(ctx.formParam("leiUt.breddegrad")).thenReturn("11.389420");
        when(ctx.formParam("leiUt.antall")).thenReturn("1");
        when(ctx.formParam("leiUt.antallHandicap")).thenReturn("1");
        when(ctx.formParam("leiUt.pris")).thenReturn("10.49");
        when(ctx.formParam("leiUt.beskrivelse")).thenReturn("Test");
        when(ctx.formParam("leiUt.brukerId")).thenReturn("1");
        when(ctx.formParam("leiUt.fra")).thenReturn(String.valueOf(fra));
        when(ctx.formParam("leiUt.til")).thenReturn(String.valueOf(til));

        Parkeringsplass actual = mockController.opprettParkeringsplass(ctx);
        Parkeringsplass expected = new Parkeringsplass(actual.getId(),59.125832, 11.389420, 1, 1, 10.49, "Test", "1", fra, til);
        assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void opprettParkeringsplass_gir_feilmelding_om_verdier_er_null() {
        Parkeringsplass plass = mockController.opprettParkeringsplass(ctx);
        assertNull(plass);
        assertEquals("Verdier mangler i ctx.formParam(): leiUt.lengdegrad" + System.getProperty("line.separator"), err.toString());
    }

    @Test
    public void opprettParkeringsplass_gir_feilmelding_om_verdier_er_paa_feil_format(){
        LocalDateTime til = LocalDateTime.now().withNano(0);
        LocalDateTime fra = LocalDateTime.now().withNano(0);

        when(ctx.formParam("leiUt.lengdegrad")).thenReturn("59.125832");
        when(ctx.formParam("leiUt.breddegrad")).thenReturn("11.389420");
        when(ctx.formParam("leiUt.antallHandicap")).thenReturn("1");
        when(ctx.formParam("leiUt.pris")).thenReturn("10.49");
        when(ctx.formParam("leiUt.beskrivelse")).thenReturn("Test");
        when(ctx.formParam("leiUt.brukerId")).thenReturn("1");
        when(ctx.formParam("leiUt.fra")).thenReturn(String.valueOf(fra));
        when(ctx.formParam("leiUt.til")).thenReturn(String.valueOf(til));

        //Vil utløse erIkkeInteger sjekken
        when(ctx.formParam("leiUt.antall")).thenReturn("Ost Smaker ganske godt");

        Parkeringsplass plass = mockController.opprettParkeringsplass(ctx);
        assertNull(plass);
        assertTrue(err.toString().contains("Verdiene er ikke ordentlige!"));
    }

    @Test
    public void lagParkeringsplass_gir_400_om_parkeringsplass_er_feil_oppsatt(){
        //enkleste måten å teste dette er ved å ikke gi noe til Context
        mockController.lagParkeringsplass(ctx);
        verify(ctx).status(400);
    }

    @Test
    public void lagParkeringsplass_gir_200_om_alt_er_riktig(){
        when(ctx.formParam("leiUt.lengdegrad")).thenReturn("59.125832");
        when(ctx.formParam("leiUt.breddegrad")).thenReturn("11.389420");
        when(ctx.formParam("leiUt.antall")).thenReturn("1");
        when(ctx.formParam("leiUt.antallHandicap")).thenReturn("1");
        when(ctx.formParam("leiUt.pris")).thenReturn("10.49");
        when(ctx.formParam("leiUt.beskrivelse")).thenReturn("Test");
        when(ctx.formParam("leiUt.brukerId")).thenReturn("1");
        when(ctx.formParam("leiUt.fra")).thenReturn(String.valueOf(LocalDateTime.now().withNano(0)));
        when(ctx.formParam("leiUt.til")).thenReturn(String.valueOf(LocalDateTime.now().withNano(0)));

        mockController.opprettParkeringsplass(ctx);
        verify(ctx).status(200);
    }

    @Test
    public void lagOrdre_fullforer_med_201(){
        Bruker bruker = loggInnFake(1234123412341234L);

        when(ctx.formParam("b.id")).thenReturn(bruker.getId());
        when(ctx.formParam("bruker.id")).thenReturn("1");
        when(ctx.formParam("ledig.parkerinsgplassId")).thenReturn("2");
        when(ctx.formParam("ledig.handicap")).thenReturn("true");
        when(ctx.formParam("ledig.fra")).thenReturn("2016-11-09T11:44:44");
        when(ctx.formParam("ledig.til")).thenReturn("2017-11-09T11:44:44");

        mockController.lagOrdre(ctx);
        verify(ctx).status(201);
    }

    @Test
    @DisplayName("Krav 6.1.9")
    public void opprettOrdre_oppretter_ordre() {
        LocalDateTime til = LocalDateTime.now().withNano(0);
        LocalDateTime fra = LocalDateTime.now().withNano(0);

        when(ctx.formParam("bruker.id")).thenReturn(id);
        when(ctx.formParam("ledig.parkerinsgplassId")).thenReturn(id);
        when(ctx.formParam("ledig.handicap")).thenReturn("true");
        when(ctx.formParam("ledig.fra")).thenReturn(String.valueOf(fra));
        when(ctx.formParam("ledig.til")).thenReturn(String.valueOf(til));

        Ordre actual = mockController.opprettOrdre(ctx);
        Ordre expected = new Ordre(actual.getId(),id, id, true, til, fra);
        assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void lagOrdre_gir_feilmelding_om_verdier_er_null() {
        mockController.opprettOrdre(ctx);
        assertEquals("Verdier mangler i ctx.formParam(): bruker.id" + System.getProperty("line.separator"), err.toString());
    }

    @Test
    @DisplayName("Krav 6.1.10")
    public void slettOrdre_sletter_ordre(){
        Bruker bruker = loggInnFake(1234123412341234L);
        LocalDateTime til = LocalDateTime.now().withNano(0);
        LocalDateTime fra = LocalDateTime.now().withNano(0);
        ArrayList<Ordre> ordreListe = new ArrayList<>();
        ordreListe.add(new Ordre(id, id, id, true, til, fra));

        //legger til ordren
        bruker.setLeierInn(ordreListe);
        assertEquals(1, bruker.getLeierInn().size());

        when(ctx.formParam("ordreId")).thenReturn(id);
        mockController.slettOrdre(ctx);
        assertEquals(0, bruker.getLeierInn().size());
    }

    @Test
    @DisplayName("Krav 6.1.13")
    public void slettParkeringsplass_sletter_parkeringsplass(){
        Bruker bruker = loggInnFake(1234123412341234L);
        LocalDateTime til = LocalDateTime.now().withNano(0);
        LocalDateTime fra = LocalDateTime.now().withNano(0);
        ArrayList<Parkeringsplass> plassListe = new ArrayList<>();
        plassListe.add(new Parkeringsplass(id, 1, 1, 1, 1,
                1, "Test parkeringsplass", "TestId", til, fra));

        //legger til parkeringsplassen
        bruker.setLeierUt(plassListe);
        mockController.setParkeringsplasser(plassListe);
        assertEquals(1, bruker.getLeierUt().size());

        when(ctx.formParam("parkeringsplassId")).thenReturn(id);
        mockController.slettParkeringsplass(ctx);
        assertEquals(0, bruker.getLeierUt().size());
    }

    @Test
    @DisplayName("Krav 6.1.16")
    public void admin_kan_slette_andres_parkeringsplasser(){
        /*
        lager to brukere, som hver setter inn én parkeringsplass.
        Bruker 1 skal prøve men ikke klare å slette en annens parkeringsplass, mens
        bruker 2 skal klare å slette en annens fordi han er en administrator.
        */
        Bruker bruker1 = new Bruker("Ikke-Admin Rulle", "1");
        Bruker bruker2 = new Administrator("Admin Kalle", "2");

        LocalDateTime til = LocalDateTime.now().withNano(0);
        LocalDateTime fra = LocalDateTime.now().withNano(0);
        ArrayList<Parkeringsplass> plassListe1 = new ArrayList<>();
        plassListe1.add(new Parkeringsplass("1", 1, 1, 1, 1,
                1, "Test parkeringsplass", "TestId", til, fra));
        bruker1.setLeierUt(plassListe1);

        ArrayList<Parkeringsplass> plassListe2 = new ArrayList<>();
        plassListe2.add(new Parkeringsplass("2", 1, 1, 1, 1,
                1, "Test parkeringsplass", "TestId", til, fra));
        bruker2.setLeierUt(plassListe2);

        ArrayList<Parkeringsplass> plassListeTotal = new ArrayList<>();
        plassListeTotal.add(plassListe1.get(0));
        plassListeTotal.add(plassListe2.get(0));
        mockController.setParkeringsplasser(plassListeTotal);

        loggInnFake(1234123412341234L, bruker1);

        //bruker1 får ikke slettet parkeringsplass til bruker2
        assertEquals(1, bruker2.getLeierUt().size());
        when(ctx.formParam("parkeringsplassId")).thenReturn("2");
        mockController.slettParkeringsplass(ctx);
        assertEquals(1, bruker2.getLeierUt().size());

        //bruker2 får slette parkerngsplass til bruker1, siden han er administrator
        assertEquals(1, bruker1.getLeierUt().size());
        when(ctx.formParam("parkeringsplassId")).thenReturn("1");
        mockController.slettParkeringsplass(ctx);
        assertEquals(0, bruker1.getLeierUt().size());
    }

    @Nested
    class kortnummer {
        Bruker bruker = new Bruker("TestBruker","1");
        @BeforeEach
        public void setup() {
            databaseRepository = new DatabaseRepository();
            databaseController = new DatabaseController(databaseRepository);

            when(ctx.formParam("redirectBrukerId")).thenReturn(bruker.getId());
            when(ctx.formParam("b.id")).thenReturn(bruker.getId());
            databaseRepository.leggTilBruker(bruker);
            databaseController.loggInn(ctx);
            databaseController.settOppBrukerinfo(ctx);
        }
        @AfterEach
        public void end() {
            databaseRepository.fjernFraDatabase("DELETE FROM personer WHERE id = '" + bruker.getId() +"'");
        }

        @Test
        @DisplayName("Krav 6.1.7.2")
        public void kan_redigere_kortnummer() {
            etEllerAnnetKortnummer("1111222233334444");
            long actual = databaseRepository.hentKortnummer(bruker.getId());
            long expected = Long.parseLong(ctx.formParam("bruker.kortnummer"));
            assertEquals(expected, actual);
        }

        @Test
        public void kortnummer_redigering_avvist_om_ugyldig_format() {
            etEllerAnnetKortnummer("1.2345");
            String actual = err.toString();
            assertTrue(actual.contains("Feil i kortnummer:"));
        }

        @ParameterizedTest
        @ValueSource(longs = {1234123412341239L, 1111111111111111L, 4572960260306001L})
        @DisplayName("Krav 6.1.8 Oddetall")
        public void kortSomEnderPaaOddetallBlirAvvist(Long numbers){
            loggInnFake(numbers);
            assertFalse(mockController.gyldigBetalingsinfo());
        }

        @ParameterizedTest
        @ValueSource(longs = {1234123412341234L, 2222222222222222L, 4572960260306002L})
        @DisplayName("krav 6.1.8 Partall")
        public void kortSomEnderPaaPartallBlirGodkjent(Long numbers){
            loggInnFake(numbers);
            assertTrue(mockController.gyldigBetalingsinfo());
        }

        public void etEllerAnnetKortnummer(String kortnr){
            when(ctx.formParam("bruker.kortnummer")).thenReturn(kortnr);
            databaseController.redigerKortnr(ctx);
        }

    }

    @Nested
    class registreringsnummer{
        String regNr = "YT34196";
        Bruker bruker;
        @BeforeEach
        public void loggInn(){
            bruker = loggInnFake(1234123412341234L);
        }

        @Test
        @DisplayName("krav 6.1.5.1")
        public void leggTilRegnr_legger_til_registreringsnumre(){
            when(ctx.formParam("leggTilRegnr")).thenReturn(regNr);
            mockController.leggTilRegnr(ctx);
            assertEquals(regNr, bruker.getRegistreringsnummer().get(0));
        }

        @Test
        @DisplayName("Krav 6.1.5.2")
        public void fjernRegnr_fjerner_registreringsnumre(){
            when(ctx.formParam("leggTilRegnr")).thenReturn(regNr);
            mockController.leggTilRegnr(ctx);
            when(ctx.formParam("slett")).thenReturn(regNr);
            mockController.slettRegnr(ctx);
            assertEquals(0, bruker.getRegistreringsnummer().size());
        }
    }

    @Test
    public void fjernParkeringsplass_fjerner_parkeringsplass(){

    }

    @Test
    @DisplayName("Krav 6.1.2")
    public void loggInn_oppdaterer_bruker(){
        Bruker bruker = new Bruker("Testus Testesen");
        //hentBruker og hentKortnummer testes i repository/Repository
        when(mockRepository.hentBruker(ctx.formParam("b.id"))).thenReturn(bruker);
        when(mockRepository.hentKortnummer(bruker.getId())).thenReturn(1234123412341234L);
        mockController.loggInn(ctx);

        assertEquals(bruker.toString(), mockController.getBruker().toString());
    }

    private Bruker loggInnFake(long kortnummer){
        Bruker bruker = new Bruker("Testus Testesen", id);
        bruker.setKortnummer(kortnummer);
        mockController.setBruker(bruker);
        List<List> listIndex = mockController.getList();
        listIndex.get(0).set(0, bruker);
        mockController.setList(listIndex);
        return bruker;
    }

    private Bruker loggInnFake(long kortnummer, Bruker bruker){
        bruker.setKortnummer(kortnummer);
        mockController.setBruker(bruker);
        List<List> listIndex = mockController.getList();
        listIndex.get(0).set(0, bruker);
        mockController.setList(listIndex);
        return bruker;
    }
}
