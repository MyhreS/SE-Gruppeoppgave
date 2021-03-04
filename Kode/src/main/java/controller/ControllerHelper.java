package controller;

import io.javalin.http.Context;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class ControllerHelper {
    protected static int erFormParamNull(List<String> parametre, Context ctx) throws NullPointerException{
        try{
            for(int i=0; i<parametre.size(); i++){
                if(ctx.formParam(parametre.get(i)) == null)
                    return i;
            }
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }
        return -1;
    }

    protected static boolean erFormParamNull(String parameter, Context ctx){
        if(ctx.formParam(parameter) == null)
            return false;

        return true;
    }

    protected static boolean erIkkeDouble(String input) {
        char[] bokstaver = input.toCharArray();
        for(int i=0; i<bokstaver.length; i++){
            if(bokstaver[i] < '0' || bokstaver[i] > '9'){
                if(bokstaver[i] == '.'){
                    continue;
                }
                return true;
            }
        }
        //vil returnere true om verdien er mindre enn maksverdien til en long, og ellers false
        return (new BigDecimal(input).abs().compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) > 0);
    }

    protected static boolean erIkkeInteger(String input){
        char[] bokstaver = input.toCharArray();
        for(int i=0; i<bokstaver.length; i++){
            if(bokstaver[i] < '0' || bokstaver[i] > '9'){
                return true;
            }
        }
        return (new BigInteger(input).abs().compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0);
    }

    protected static boolean erIkkeLong(String input){
        char[] bokstaver = input.toCharArray();
        for(int i=0; i<bokstaver.length; i++){
            if(bokstaver[i] < '0' || bokstaver[i] > '9'){
                return true;
            }
        }
        return (new BigInteger(input).abs().compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0);
    }
}
