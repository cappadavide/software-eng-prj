package model;

public class FotoModel {
    private String nomeFile;
    private String pathFile;

    public FotoModel(String nomeFile, String pathFile){
        this.nomeFile=nomeFile;
        this.pathFile=pathFile;
    }
    
    public String getNomeFile() {
        return nomeFile;
    }

    public void setNomeFile(String nomeFile) {
        this.nomeFile=nomeFile;
    }
    
    public String getPathFile() {
        return pathFile;
    }

    public void setPathFile(String pathFile) {
        this.pathFile=pathFile;
    }

}