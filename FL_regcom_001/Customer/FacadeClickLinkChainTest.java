package com.regcom.webservice.producer;

import java.util.ArrayList;
import java.util.List;

public class FacadeClickLinkChainTest {

	
	public static void main(String[] args) {
				
		//USE CASE 1  - the same as is in the version FL_regcom_001_V_1_3_4,   that run at the first line of the 
		//topics.txt
		////    cars,advanced search,auto.com,2018 honda accord
		
		runUseCase1();
			
		//USE CASE 2 - Alternative use case
				runUseCase2();
				
	}

	

	private static void runUseCase1() {
		////    cars,advanced search,auto.com,2018 honda accord
		String urlInitial="google.com";
		boolean typeSearchIntoUrlInitialPage = true;
		String textToTypeInTheInitialPage="cars";
		
		List  keywordList = new ArrayList<KeyWordDto>();
		KeyWordDto keyWord1 = new KeyWordDto();
		keyWord1.setKeyWord("advanced search");
		keyWord1.setOpenWindowInSameBorwserWindow(true);
		keywordList.add(keyWord1);
		
		KeyWordDto keyWord2 = new KeyWordDto();
		keyWord2.setKeyWord("auto.com");
		keyWord2.setOpenWindowInSameBorwserWindow(true);
		keywordList.add(keyWord2);
		
		KeyWordDto keyWord3 = new KeyWordDto();
		keyWord3.setKeyWord("2018 honda accord");
		keyWord3.setOpenWindowInSameBorwserWindow(true);
		keywordList.add(keyWord3);
		
		
		FacadeClickLinkChain.clickLinkChain(urlInitial, typeSearchIntoUrlInitialPage, textToTypeInTheInitialPage, keywordList);
		
		
	}
	
	
	
	private static void runUseCase2() {
		String urlInitial="google.com?q=Official+Ferrari+website";
		boolean typeSearchIntoUrlInitialPage = false;
		String textToTypeInTheInitialPage="";
		
		List  keywordList = new ArrayList<KeyWordDto>();
		KeyWordDto keyWord1 = new KeyWordDto();
		keyWord1.setKeyWord("Ferrari UK - Official Dealers UK - HR Owen");
		keyWord1.setOpenWindowInSameBorwserWindow(false);//This site will open in new browser tab, ant the focus stay in the google page.
		keywordList.add(keyWord1);
		
		KeyWordDto keyWord2 = new KeyWordDto();
		keyWord2.setKeyWord("Official Ferrari website - Ferrari Owners' Club of New Zealand");
		keyWord2.setOpenWindowInSameBorwserWindow(false);//This site will open in new browser tab, ant the focus stay in the google page.
		keywordList.add(keyWord2);
		
		
		// When this method finishc all browser tabs will be closed.
		
		FacadeClickLinkChain.clickLinkChain(urlInitial, typeSearchIntoUrlInitialPage, textToTypeInTheInitialPage, keywordList);
		
	}
	
	

}
