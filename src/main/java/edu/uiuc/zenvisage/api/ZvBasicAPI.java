package edu.uiuc.zenvisage.api;

import edu.uiuc.zenvisage.service.ZvMain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ZvBasicAPI {

	@Autowired
	private ZvMain zvMain;



    public ZvBasicAPI(){

	}



}
