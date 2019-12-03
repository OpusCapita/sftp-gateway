package com.opuscapita.web.service;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class JsLoaderService {

    private final String path = "/usr/app/built/";

    public List<String> getResourceFiles(String path) throws IOException, NullPointerException {
        List<String> filenames = new ArrayList<>();
        try (
                InputStream in = getResourceAsStream(path);
                BufferedReader br = new BufferedReader(new InputStreamReader(in))
        ) {
            String resource;

            while ((resource = br.readLine()) != null) {
                filenames.add(resource);
            }
        }

        return filenames;
    }

    public InputStream getResourceAsStream(String resource) {
        final InputStream in
                = getContextClassLoader().getResourceAsStream(resource);

        return in == null ? getClass().getResourceAsStream(resource) : in;
    }

    public String getResourceFromFileSystem(String resource) throws IOException, NullPointerException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(this.path + resource);
        return FileUtils.readFileToString(file, "UTF-8");
    }

    ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
