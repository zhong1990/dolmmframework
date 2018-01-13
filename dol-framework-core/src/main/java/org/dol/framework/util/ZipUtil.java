package org.dol.framework.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ZipUtil {

    public static Map<String, String> readZip(File file, String charsetName) throws ZipException, IOException {
        ZipFile zipFile = new ZipFile(file);
        Map<String, String> map = new HashMap<String, String>(zipFile.size());
        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = entries.nextElement();
            if (zipEntry.isDirectory()) {
                continue;
            }
            InputStream inputStream = zipFile.getInputStream(zipEntry);
            InputStreamReader reader = charsetName == null ? reader = new InputStreamReader(inputStream) : new InputStreamReader(inputStream, charsetName);
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            map.put(zipEntry.getName(), stringBuilder.toString());
            FileUtil.close(bufferedReader);
            FileUtil.close(reader);
            FileUtil.close(inputStream);
        }
        FileUtil.close(zipFile);
        return map;

    }

    public static Map<String, String> readZip(File file) throws ZipException, IOException {
        return readZip(file, null);

    }

    public static Map<String, String> readZip(String fileName, String charsetName) throws ZipException, IOException {
        return readZip(new File(fileName), charsetName);
    }

}
