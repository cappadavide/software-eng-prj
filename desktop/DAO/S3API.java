package DAO;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;
import model.FotoModel;

public class S3API {
    private static final String prefix = "https://consigliaviaggi.s3.eu-west-3.amazonaws.com/";
    private static String nameBucket = "consigliaviaggi";
    private static AWSCredentials credentials = new BasicAWSCredentials("AKIAJIZF2YNH3ZKJX64Q", "ui57cXXe1oSwrvkpgp2hqXP+INPmNi1AziNZW01D");
    private static AmazonS3 s3Client = AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(Regions.EU_WEST_3)
            .build();

    public void inserisciFoto(ArrayList<FotoModel> lista) {
        Iterator<FotoModel> iteratore = lista.iterator();
        while (iteratore.hasNext()) {
            File pathFile = new File(iteratore.next().getPathFile().substring(5));
            caricaFoto(pathFile);
        }
    }

    public void caricaFoto(File nomeFile) {
        try {
           PutObjectRequest richiesta = new PutObjectRequest(nameBucket, nomeFile.getName(), nomeFile);
            richiesta.setCannedAcl(CannedAccessControlList.PublicRead);
            s3Client.putObject(richiesta);
        } catch (AmazonServiceException e) {
            throw new AmazonServiceException(e.getErrorMessage());
        }
    }

    public void eliminaFotoS3(ArrayList<FotoModel> lista) {
        Iterator<FotoModel> iteratore = lista.iterator();
        while (iteratore.hasNext()) {
            String file = iteratore.next().getPathFile();
            if (Pattern.matches("https://.*", file)) {
                File pathFile = new File (file);
                eliminaFoto(pathFile);
            }
        }
    }

    public void eliminaFoto(File pathFile) {
        s3Client.deleteObject(nameBucket, pathFile.getName());
    }
}
