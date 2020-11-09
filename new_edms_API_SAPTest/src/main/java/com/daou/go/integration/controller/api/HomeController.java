package com.daou.go.integration.controller.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.daou.go.integration.helper.XmlFormatter;
import com.daou.go.integration.service.SapJcoRfcManager;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

@Controller
public class HomeController {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	SapJcoRfcManager sapJcoRfcManager;
	
	@ResponseBody
	@RequestMapping(value = "/ajax/test2")
	public void ajax_test2(@RequestParam HashMap<String, Object> reqmap, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws JCoException{
		System.out.println("@RequestMapping(value = /ajax/test2)");
		if (!reqmap.isEmpty()) {
			System.out.println("@RequestParam reqmap ==>");
			System.out.println("{");
			for (String mapkey : reqmap.keySet()) {
				if (!"".equals("" + reqmap.get(mapkey)) && null != reqmap.get(mapkey)) {
					String resultStr = mapkey + "=" + reqmap.get(mapkey);
					System.out.println(resultStr);
				}
			}
			System.out.println("}");
		}
		
		JCoDestination destination = sapJcoRfcManager.getJCoDestination();
		
		List<Map<String, Object>> returnList = new ArrayList<Map<String,Object>>();
		
		Map<String, Object> submitData = new HashMap<String, Object>();
		String pernr = reqmap.get("I_PERNR").toString();
		int year = Integer.parseInt(reqmap.get("I_YEAR").toString());
		
		for(int tempYear = (year - 1); tempYear < (year + 2); tempYear++) {
			submitData = new HashMap<String, Object>();
			submitData.put("I_PERNR", pernr);
			submitData.put("I_YEAR", tempYear);
			
			//휴일 정보
			String functionName = "ZHR_HOLY_YEAR";
			JCoFunction function = destination.getRepository().getFunction(functionName);
			
			for (String mapkey : submitData.keySet()) {
				function.getImportParameterList().setValue(mapkey, submitData.get(mapkey));
			}
			
			System.out.println("SAP 전달 Param : {" + XmlFormatter.format(function.toXML()) + "}");
			Map<String, String> sapDataMap = new HashMap<String, String>();
			
			try {
				System.out.println(functionName + " - Import : {}" + function.toXML());
				function.execute(destination);
				System.out.println(functionName + " - Export : {}" + function.toXML());
				
				JCoStructure E_RESULT = function.getExportParameterList().getStructure("E_RETURN");
				
				String result = E_RESULT.getString("TYPE");
				String message = E_RESULT.getString("MESSAGE");
				
				sapDataMap.put("TYPE_YEAR",    result);   //메시지유형(S 성공, E 오류)
				sapDataMap.put("MESSAGE_YEAR", message);  //메시지 텍스트
				
				JCoTable jCoTable = function.getTableParameterList().getTable("T_ZHRS0130");
				for (int i = 0; i < jCoTable.getNumRows(); i++) {
					jCoTable.setRow(i);
					Map<String, Object> dataMap = new HashMap<String, Object>();
					
					dataMap.put("YYMMDD", jCoTable.getString("YYMMDD"));
					dataMap.put("HOLY_NM", jCoTable.getString("HOLY_NM"));
					
					returnList.add(dataMap);
				}
				
				System.out.println(functionName + " 성공여부(TYPE) : "     + result);
				System.out.println(functionName + " 메시지(MESSAGE) : "   + message);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		
		int tempCount = 0;
		for (Map<String, Object> map : returnList) {
			System.out.println("#" + tempCount + " Result");
			System.out.println("{");
			for (String mapkey : map.keySet()) {
				if (!"".equals("" + map.get(mapkey)) && null != map.get(mapkey)) {
					String resultStr = mapkey + "=" + map.get(mapkey);
					System.out.println(resultStr);
				}
			}
			tempCount++;
			System.out.println("}");
		}
	}
	
}
