package com.daou.go.integration.service;

import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.ext.DestinationDataProvider;

@Component
public class SapJcoRfcManager {

	private static final Logger logger = LoggerFactory.getLogger(SapJcoRfcManager.class);

//	private final static String DESTINATION = "PRD"; // 운영
	private final static String DESTINATION = "QAS"; // 개발

//	@Value("${jco_ashost:r3.eduhansol.co.kr}")
//	@Value("${jco_ashost:202.89.125.97}")
	private String jco_ashost = "202.89.125.97";

//	@Value("${jco_gwhost:202.89.125.66}")
//	@Value("${jco_gwhost:202.89.125.97}")
//	private String jco_gwhost;

//	@Value("${jco_sysnr:07}")
//	@Value("${jco_sysnr:01}")
	private String jco_sysnr = "01";

//	@Value("${jco_client:100}")
//	@Value("${jco_client:100}")
	private String jco_client = "100";

//	@Value("${jco_user:hsdr1007}")
//	@Value("${jco_user:hsdr1007}")
	private String jco_user = "hsdr1007";

//	@Value("${jco_passwd:inithsdr1@}")
//	@Value("${jco_passwd:inithsdr1@}")
	private String jco_passwd = "inithsdr1@";

//	@Value("${jco_lang:KO}")
//	@Value("${jco_lang:KO}")
	private String jco_lang = "KO";

//	@Value("${jco_client.group:RFCHansolGyoyook}")
//	@Value("${jco_client.group:RFCHansolGyoyook}")
//	private String jco_clientGroup;

	@PostConstruct
	public void init() {
		HansolDestinationDataProvider provider = new HansolDestinationDataProvider();
		provider.addDestination(DESTINATION, getDestinationProperties());
		com.sap.conn.jco.ext.Environment.registerDestinationDataProvider(provider);
	}

	private Properties getDestinationProperties() {
		Properties connectProperties = new Properties();
		connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, jco_ashost); // 내부아이피
//		connectProperties.setProperty(DestinationDataProvider.JCO_GWHOST, jco_gwhost); // 외부아이피
		connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, jco_sysnr);
		connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, jco_client);
		connectProperties.setProperty(DestinationDataProvider.JCO_USER, jco_user);
		connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, jco_passwd);
		connectProperties.setProperty(DestinationDataProvider.JCO_LANG, jco_lang);
//		connectProperties.setProperty(DestinationDataProvider.JCO_GROUP, jco_clientGroup);
		return connectProperties;
	}

	public JCoDestination getJCoDestination() throws JCoException {

		JCoDestination destination = JCoDestinationManager.getDestination(DESTINATION);

		return destination;
	}

	public JCoFunction getJCoFuntion(String functionName) throws JCoException {

		JCoDestination destination = getJCoDestination();

		JCoFunction function = destination.getRepository().getFunction(functionName);
		if (function == null) {
			logger.error(functionName + " is not found in SAP.");
			throw new RuntimeException(functionName + " is not found in SAP.");
		}
		
		return function;
	}
}