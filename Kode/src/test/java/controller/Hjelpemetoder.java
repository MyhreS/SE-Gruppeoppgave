package controller;

import io.javalin.http.Context;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import repository.DatabaseRepository;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Hjelpemetoder {
    Context ctx = mock(Context.class);
    DatabaseRepository repo = new DatabaseRepository();

    @ParameterizedTest
    @MethodSource("SQLtidOgJavaTid")
    public void testJavaTilSQLTid(LocalDateTime javaTid, long sqlTid){
        assertEquals(sqlTid, repo.konverterFraJavatilSQLTid(javaTid));
    }

    @ParameterizedTest
    @MethodSource("SQLtidOgJavaTid")
    public void testSQLTilJavaTid(LocalDateTime javaTid, long SQLtid){
        assertEquals(javaTid, repo.konverterFraSQLtilJavaTid(SQLtid));
    }

    private static Stream<Arguments> SQLtidOgJavaTid(){
        return Stream.of(
                Arguments.of(LocalDateTime.of(2020, 11, 12, 15, 0), 1605193200L),
                Arguments.of(LocalDateTime.of(1988, 5, 1, 23, 58), 578534280L),
                Arguments.of(LocalDateTime.of(2000, 1, 2, 0, 0), 946771200L),
                Arguments.of(LocalDateTime.of(2090, 12, 31, 5, 11), 3818380260L)
                );
    }

    @Test
    @DisplayName("Formparam er null")
    public void formparam_er_null() {
        when(ctx.formParam("test")).thenReturn(null);
        boolean actual = ControllerHelper.erFormParamNull("test", ctx);
        assertFalse(actual);
    }

    @Test
    @DisplayName("Formparam er ikke null")
    public void formparam_er_ikke_null() {
        when(ctx.formParam("test")).thenReturn("test");
        boolean actual = ControllerHelper.erFormParamNull("test", ctx);
        assertTrue(actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.5", "5.5", "9.5"})
    @DisplayName("Er en double")
    public void er_det_en_double(String input) {
        assertFalse(ControllerHelper.erIkkeDouble(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0,5", "5,5", "9A"})
    @DisplayName("Er ikke en double")
    public void er_det_ikke_en_double(String input) {
        assertTrue(ControllerHelper.erIkkeDouble(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "5", "9"})
    @DisplayName("Er en integer")
    public void er_det_en_integer(String input) {
        assertFalse(ControllerHelper.erIkkeInteger(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.5", "5.5", "9.5", "1111222233334444", "String"})
    @DisplayName("Er ikke en integer")
    public void er_det_ikke_en_integer(String input) {
        assertTrue(ControllerHelper.erIkkeInteger(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1111222233334444"})
    @DisplayName("Er en long")
    public void er_det_en_long(String input) {
        assertFalse(ControllerHelper.erIkkeLong(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-0.5", "-5.5", "-9.5", "String"})
    @DisplayName("Er ikke en long")
    public void er_det_ikke_en_long(String input) {
        assertTrue(ControllerHelper.erIkkeLong(input));
    }



}
