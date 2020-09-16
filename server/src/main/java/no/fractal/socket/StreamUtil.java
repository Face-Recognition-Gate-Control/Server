package no.fractal.socket;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {

	public void writeStreamToFile(InputStream in, String filename, String path, int filesize) {

		try {
			FileOutputStream fos = new FileOutputStream(path + filename);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			byte[] filebuffer = new byte[4096];
			int remaining = filesize;
			int read = 0;
			int totalRead = 0;
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
