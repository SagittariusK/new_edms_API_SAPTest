package com.daou.go.integration.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.sap.conn.jco.ext.DataProviderException;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;

public class HansolDestinationDataProvider implements DestinationDataProvider {

	Map<String, Properties> propertiesForDestinationName = new HashMap<String, Properties>();

    @Override
    public Properties getDestinationProperties(String destinationName) throws DataProviderException {
        if (propertiesForDestinationName.containsKey(destinationName)) {
            return propertiesForDestinationName.get(destinationName);
        } else {
            throw new RuntimeException("JCo destination not found: " + destinationName);
        }
    }

    @Override
    public void setDestinationDataEventListener(DestinationDataEventListener arg0) {

    }

    @Override
    public boolean supportsEvents() {
        return false;
    }

    public void addDestination(String destinationName, Properties properties) {
        propertiesForDestinationName.put(destinationName, properties);
    }
}