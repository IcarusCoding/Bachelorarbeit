package de.intelligence.bachelorarbeit.simplifx.config;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.xml.sax.SAXException;

import de.intelligence.bachelorarbeit.simplifx.exception.InvalidConfigFileException;
import de.intelligence.bachelorarbeit.simplifx.utils.Conditions;
import de.intelligence.bachelorarbeit.simplifx.utils.Prefix;

public final class PropertyRegistry {

    private final ClassLoader sourceLoader;
    private final Properties properties;

    public PropertyRegistry(ClassLoader sourceLoader) {
        this.sourceLoader = sourceLoader;
        this.properties = new Properties();
    }

    public void loadFrom(String path) {
        Conditions.checkNull(path, "path must not be null.");
        if (!this.tryLoad(path)) {
            if (!path.endsWith(Prefix.XML_FILE_EXTENSION) && !path.endsWith(Prefix.PROPERTIES_FILE_EXTENSION)) {
                if (!this.tryLoad(path + Prefix.XML_FILE_EXTENSION) & !this.tryLoad(path + Prefix.PROPERTIES_FILE_EXTENSION)) {
                    throw new InvalidConfigFileException("Could not load configuration file " + path + ". Path or configuration file format was invalid.");
                }
            }
        }
    }

    private boolean tryLoad(String path) {
        try (InputStream input = this.sourceLoader.getResourceAsStream(path)) {
            if (input == null) {
                return false;
            } else {
                final Properties properties = this.load(input);
                properties.forEach((k, v) -> {
                    if (this.properties.containsKey(k)) {
                        System.out.println("WARNING: REPLACING VALUE " + this.properties.get(k) + " WITH " + v + " FOR KEY " + k);
                    }
                });
                this.properties.putAll(properties);
            }
        } catch (IOException ex) {
            return false;
        }
        return true;
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
