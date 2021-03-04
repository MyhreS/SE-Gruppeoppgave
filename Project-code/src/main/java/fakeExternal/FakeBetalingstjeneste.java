package fakeExternal;

public class FakeBetalingstjeneste {
    public static boolean sjekkKort(long kortNummer){
        return kortNummer % 2 != 1;
    }
}
