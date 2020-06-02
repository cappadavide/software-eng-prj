package com.example.consigliaviaggi;

import com.example.consigliaviaggi.presenter.RicercaPresenter;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class RicercaPresenterTest {

    private RicercaPresenter ricercaPresenter;

    @Before
    public void setUp(){
        ricercaPresenter=new RicercaPresenter(null,null);
    }

    @Test
    public void testbuildUrlString_3_4_5_6_10_12_13_17_18_20_21_25_26_30(){
        String oracolo="nome=b&categoria=c&indirizzo=a&sottocategoria=d&rating=2.0&prezzoda=e&prezzoa=f";
        assertEquals(ricercaPresenter.buildUrlString("a","b","c","d",2,"e","f"),oracolo);
    }
    @Test
    public void testbuildUrlString_3_4_5_8_10_12_15_17_18_20_23_25_28_30(){
        String oracolo="nome=undef&categoria=c&indirizzo=undef&sottocategoria=d&rating=2.0&prezzoda=0.0&prezzoa=0.0";
        assertEquals(ricercaPresenter.buildUrlString("","","c","d",2,"",""),oracolo);
    }
    @Test
    public void testbuildUrlString_3_4_5_6_10_12_15_17_18_20_21_25_28_30(){
        String oracolo="nome=a&categoria=c&indirizzo=undef&sottocategoria=d&rating=3.0&prezzoda=1.0&prezzoa=0.0";
        assertEquals(ricercaPresenter.buildUrlString("","a","c","d",3,"1.0",""),oracolo);
    }
    @Test
    public void testbuildUrlString_3_4_5_8_10_12_13_17_18_20_23_25_26_30(){
        String oracolo="nome=undef&categoria=c&indirizzo=a&sottocategoria=d&rating=3.0&prezzoda=0.0&prezzoa=1.0";
        assertEquals(ricercaPresenter.buildUrlString("a","","c","d",3,"","1.0"),oracolo);
    }
}