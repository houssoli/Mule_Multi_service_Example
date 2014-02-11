package com.mycompany.app.processor;

import com.mycompany.app.common.Data;


/**
 *  This service gets an uprocessed Data object and approves it by
 *  concatenating an approvement message to the payload. 
 */
public class Approver {

    public Data approve(Data data) throws Exception {
        System.out.println("----- Approver got Data: " + data);
        data.setRawData(data.getRawData() + "-Approved");
		data.setState(2);
		try{Thread.sleep(2000);	}catch (Exception e){}
        return data;
    }    
}