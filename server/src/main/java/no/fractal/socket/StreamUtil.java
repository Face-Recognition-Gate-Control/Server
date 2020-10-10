package no.fractal.socket;

import java.io.*;

public class StreamUtil {

    /**
     * Writes stream data into a file. The path must include the path + filename.
     *
     * @param in       inpustream to read from
     * @param path     the path+filename for tile to write to.
     * @param filesize how many bytes to read from the stream
     */
    public static void writeStreamToFile(InputStream in, String path, int filesize) {
        try {
            FileOutputStream     fos        = new FileOutputStream(path);
            BufferedOutputStream bos        = new BufferedOutputStream(fos);
            byte[]               filebuffer = new byte[4096];
            int                  remaining  = filesize;
            int                  read       = 0;
            int                  totalRead  = 0;
            while ((read = in.read(filebuffer, 0, Math.min(filebuffer.length, remaining))) > 0) {
                totalRead += read;
                remaining -= read;
                System.out.printf("%.2f %n", (float) totalRead / filesize * 100);
                bos.write(filebuffer, 0, read);
            }
            bos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
