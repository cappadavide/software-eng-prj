package com.consigliaviaggi;

import junit.framework.TestCase;
import presenter.RicercaStrutturaPresenter;

public class RicercaStrutturaPresenterTest extends TestCase {
    
    public void testRoundConNumeroDiCifreSignificativeNegativo() {
        try {
            RicercaStrutturaPresenter.round(3.7, -29);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    public void testRoundConNumeroDiCifreSignificativeUgualeAMAX_INT(){
        double oracolo = 1.0;
        try {
            assertEquals(RicercaStrutturaPresenter.round(2.4, 2147483647), oracolo);
        }
        catch (IllegalArgumentException e){
            fail();
        }
    }
    
    public void testRoundConNumeroDiCifreSignificativetTraZeroEMAX_INT(){
        double oracolo=1.0;
        try {
            assertEquals(RicercaStrutturaPresenter.round(2.478, 1073741824), oracolo);
        }
        catch (IllegalArgumentException e){
            fail();
        }
    }
    
    public void testRoundConNumeroDiCifreSignificativeVicinoAZero(){
        double oracolo=2.5;
        try {
            assertEquals(RicercaStrutturaPresenter.round(2.478, 1), oracolo);
        }
        catch (IllegalArgumentException e){
            fail();
        }
    }
    
    public void testRoundConNumeroDiCifreSignificativeVicinoAMAX_INT(){
        double oracolo = 1.0;
        try {
            assertEquals(RicercaStrutturaPresenter.round(2.478, 2147481000), oracolo);
        }
        catch (IllegalArgumentException e){
            fail();
        }
    }

}
