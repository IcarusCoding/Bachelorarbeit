package de.intelligence.bachelorarbeit.simplifx.config;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.ProtectionDomain;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import de.intelligence.bachelorarbeit.simplifx.exception.InvalidConfigFileException;
import de.intelligence.bachelorarbeit.simplifx.utils.Conditions;
import de.intelligence.bachelorarbeit.simplifx.utils.Prefix;

public final class PropertyRegistry {

    private static final Logger LOG = LogManager.getLogger(PropertyRegistry.class);

    private final ProtectionDomain domain;
    private final ClassLoader sourceLoader;
    private final Properties properties;

    public PropertyRegistry(Object source) {
        this.domain = source.getClass().getProtectionDomain();
        this.sourceLoader = source.getClass().getClassLoader();
        this.properties = new Properties();
    }

    public void loadFromClasspath(String path) {
        Conditions.checkNull(path, "path must not be null.");
        boolean successful = false;
        for (String s : this.createPossiblePaths(path)) {
            if (this.tryLoadFromClasspath(s)) {
                successful = true;
                break;
            }
        }
        if (!successful) {
            throw new InvalidConfigFileException("Could not load internal configuration file \"" + path + "\". Path or configuration file format was invalid.");
        }
    }

    public void loadFromOutside(String path) {
        Conditions.checkNull(path, "path must not be null.");
        boolean successful = false;
        for (String s : this.createPossiblePaths(path)) {
            if (this.tryLoadFromOutside(s)) {
                successful = true;
                break;
            }
        }
        if (!successful) {
            throw new InvalidConfigFileException("Could not load external configuration file \"" + path + "\". Path or configuration file format was invalid.");
        }
    }

    private String[] createPossiblePaths(String base) {
        if (base.endsWith(Prefix.PROPERTIES_FILE_EXTENSION) || base.endsWith(Prefix.XML_FILE_EXTENSION)) {
            return new String[]{base};
        }
        return new String[]{base, base + Prefix.PROPERTIES_FILE_EXTENSION, base + Prefix.XML_FILE_EXTENSION};
    }

    private boolean tryLoadFromOutside(String path) {
        File f = new File(path);
        if (!f.isAbsolute()) {
            String configDir = System.getProperty("config.dir");
            if (configDir == null) {
                configDir = new File(this.domain.getCodeSource().getLocation().getFile()).getParent();
            }
            if (configDir == null) {
                configDir = System.getProperty("user.dir");
            }
            if (configDir == null) {
                return false;
            }
            f = new File(configDir, path);
        }
        try (InputStream input = new FileInputStream(f)) {
            this.handleProperties(this.load(input));
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    private boolean tryLoadFromClasspath(String path) {
        try (InputStream input = this.sourceLoader.getResourceAsStream(path)) {
            if (input == null) {
                return false;
            } else {
                this.handleProperties(this.load(input));
            }
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    private void handleProperties(Properties properties) {
        properties.forEach((k, v) -> {
            if (this.properties.containsKey(k)) {
                LOG.warn("Detected duplicate configuration value! Replacing value \"{}\" with \"{}\" for key \"{}\".",
                        this.properties.get(k), v, k);
            }
        });
        this.properties.putAll(properties);
    }

    private Properties load(InputStream input) throws IOException {
        final Properties properties = new Properties();
        final byte[] bytes = input.readAllBytes();
        final ByteArrayInputStream bytesXMLIn = new ByteArrayInputStream(bytes);
        final ByteArrayInputStream bytesPropIn = new ByteArrayInputStream(bytes);
        try (bytesXMLIn; bytesPropIn) {
            final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            builder.setErrorHandler(null);
            builder.parse(bytesXMLIn);
            properties.loadFromXML(bytesPropIn);
        } catch (SAXException | ParserConfigurationException e) {
            properties.load(bytesPropIn);
        }
        return properties;
    }

    public Properties getReadOnlyProperties() {
        return new ReadOnlyPropertiesWrapper(this.properties);
    }

    public String getForKey(String key, String def) {
        return this.properties.getProperty(key, def);
    }

}
