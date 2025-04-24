package models;

import java.io.*;

public class Flight implements Serializable {

    @Serial
    private static final long serialVersionUID = 6529685098267757691L;

    public void save(String path, String filename) throws IOException {
        FileOutputStream fout = new FileOutputStream(filename.concat(".umasflight"));
        ObjectOutputStream oos = new ObjectOutputStream(fout);

        oos.writeObject(this);
        oos.flush();
        oos.close();
    }

    public static Flight read(String path, String filename) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(filename.concat(".umasflight"));
        ObjectInputStream ois = new ObjectInputStream(fis);

        return (Flight) ois.readObject();
    }

}
