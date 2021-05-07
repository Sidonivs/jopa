/**
 * Copyright (C) 2020 Czech Technical University in Prague
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package cz.cvut.kbss.jopa.loaders;

import cz.cvut.kbss.jopa.exceptions.OWLPersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Processes classes available to the current classloader.
 */
class ClasspathScanner {

    private static final Logger LOG = LoggerFactory.getLogger(ClasspathScanner.class);

    private static final String JAR_FILE_SUFFIX = ".jar";
    private static final String CLASS_FILE_SUFFIX = ".class";

    private final List<Consumer<Class<?>>> listeners = new ArrayList<>();

    private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    void addListener(Consumer<Class<?>> listener) {
        listeners.add(listener);
    }

    /**
     * Inspired by https://github.com/ddopson/java-class-enumerator
     */
    void processClasses(String scanPath) {
        try {
            Enumeration<URL> urls = classLoader.getResources(scanPath.replace('.', '/'));
            while (urls.hasMoreElements()) {
                final URL url = urls.nextElement();
                if (isJar(url.toString())) {
                    processJarFile(sanitizePath(url), scanPath);
                } else {
                    processDirectory(new File(getUrlAsUri(url).getPath()), scanPath);
                }
            }
            // Scan jar files on classpath
            Enumeration<URL> resources = classLoader.getResources(".");
            while (resources.hasMoreElements()) {
                URL resourceURL = resources.nextElement();
                if (isJar(resourceURL.toString()))
                    processJarFile(sanitizePath(resourceURL), scanPath);
            }
        } catch (IOException e) {
            throw new OWLPersistenceException("Unable to scan packages for entity classes.", e);
        }
    }

    /**
     * Handles possible non-ascii character encoding in the specified URL.
     * @param url Resource URL (presumably leading to a local file)
     * @return Decoded argument
     * @throws UnsupportedEncodingException Should not happen, using standard UTF-8 encoding
     */
    private static String sanitizePath(URL url) throws UnsupportedEncodingException {
        return URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8.toString());
    }

    private static boolean isJar(String filePath) {
        return filePath.startsWith("jar:") || filePath.endsWith(JAR_FILE_SUFFIX);
    }

    private static URI getUrlAsUri(URL url) {
        try {
            // Transformation to URI handles encoding, e.g. of whitespaces in the path
            return url.toURI();
        } catch (URISyntaxException ex) {
            throw new OWLPersistenceException(
                    "Unable to scan resource " + url + ". It is not a valid URI.", ex);
        }
    }

    private void processJarFile(String jarResource, String packageName) {
        final String relPath = packageName.replace('.', '/');
        final String jarPath = jarResource.replaceFirst("[.]jar[!].*", JAR_FILE_SUFFIX)
                                          .replaceFirst("file:", "");

        LOG.trace("Scanning jar file {} for entity classes.", jarPath);
        try (final JarFile jarFile = new JarFile(jarPath)) {
            final Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();
                final String entryName = entry.getName();
                String className = null;
                if (entryName.endsWith(CLASS_FILE_SUFFIX) && entryName.contains(relPath)) {
                    className = entryName.substring(entryName.indexOf(relPath));
                    className = className.replace('/', '.').replace('\\', '.');
                    className = className.substring(0, className.length() - CLASS_FILE_SUFFIX.length());
                }
                if (className != null) {
                    processClass(className);
                }
            }
        } catch (IOException e) {
            throw new OWLPersistenceException("Unexpected IOException reading JAR File " + jarPath, e);
        }
    }

    private void processClass(String className) {
        try {
            final Class<?> cls = Class.forName(className, true, classLoader);
            listeners.forEach(listener -> listener.accept(cls));
        } catch (ClassNotFoundException e) {
            throw new OWLPersistenceException("Unexpected ClassNotFoundException when scanning for entities.", e);
        }
    }

    private void processDirectory(File dir, String packageName)
            throws MalformedURLException, UnsupportedEncodingException {
        LOG.trace("Scanning directory {} for entity classes.", dir);
        // Get the list of the files contained in the package
        final String[] files = dir.list();
        if (files == null) {
            return;
        }
        for (String fileName : files) {
            String className = null;
            // we are only interested in .class files
            if (fileName.endsWith(CLASS_FILE_SUFFIX)) {
                // removes the .class extension
                className = packageName + '.' + fileName.substring(0, fileName.length() - 6);
            }
            if (className != null) {
                processClass(className);
            }
            final File subDir = new File(dir, fileName);
            if (subDir.isDirectory()) {
                processDirectory(subDir, packageName + (!packageName.isEmpty() ? '.' : "") + fileName);
            } else if (isJar(subDir.getAbsolutePath())) {
                processJarFile(sanitizePath(subDir.toURI().toURL()), packageName);
            }
        }
    }
}
