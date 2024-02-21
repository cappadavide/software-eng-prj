package model;

public class CoordinateModel {
    private double latitudine;
    private double longitudine;
    
    public CoordinateModel(double latitudine, double longitudine){
        this.latitudine=latitudine;
        this.longitudine=longitudine;
    }
    
    public void setLatitudine(double latitudine){
        this.latitudine=latitudine;
    }
    
    public double getLatitudine(){
        return latitudine;
    }
    
    public void setLongitudine(double longitudine){
        this.longitudine=longitudine;
    }
    
     public double getLongitudine(){
        return longitudine;
    }

    @Override
    public String toString() {
        return latitudine + " " + longitudine;
    }
    
}
