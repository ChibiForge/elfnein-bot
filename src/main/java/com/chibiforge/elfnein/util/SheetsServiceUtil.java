package com.chibiforge.elfnein.util;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;

public class SheetsServiceUtil {
    private static final String APPLICATION_NAME = "ElfneinBot";

    public static Sheets getSheetsService() throws IOException, GeneralSecurityException {
        Credential credential = GoogleAuthorizeUtil.authorize();
        return new Sheets.Builder(
          GoogleNetHttpTransport.newTrustedTransport(), 
          JacksonFactory.getDefaultInstance(), credential)
          .setApplicationName(APPLICATION_NAME)
          .build();
    }
    
	public static String[][] getData(String spreadSheetID,String sheetName, String range){
		String[][] arrayData = null;
    	try {
    		Sheets sheets = getSheetsService();
        	List<List<Object>> data = sheets.spreadsheets().values()
                    .get(spreadSheetID,sheetName+"!"+range)
                    .execute().getValues();
            
        	arrayData = convertToArray(data);
        	System.out.println(arrayData[0][0]);
    	}catch(Exception e) {
    		System.out.println("NOT WORK");
    		e.printStackTrace();
    	}
    	
    	return arrayData;
    	
    }
    
    private static String[][] convertToArray(List<List<Object>> data) {
        String[][] array = new String[data.size()][];

        int i = 0;
        for (List<Object> row : data) {
            array[i++] = row.toArray(new String[row.size()]);
        }
        return array;
    }
}