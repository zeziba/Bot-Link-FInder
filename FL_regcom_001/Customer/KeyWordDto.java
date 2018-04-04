package com.regcom.webservice.producer;

public class KeyWordDto {
	
	private String keyWord; //example: "website,Ferrari UK - Official Dealers UK - HR Owen"
	
	/*
	 * If openWindowInSameBorwserWindow=true => the software will open the link for this keywork in the same browser windos 
	 * as it make now
	 * 
	 * If openWindowInSameBorwserWindow=false =>the software will open the link for this keywork in 
	 * a new browser tab. In this case, when the method finish thats necessary close all Browser tabs 
	 * as it make now
	 * 
	 */
	private boolean openWindowInSameBorwserWindow ;
	
	
	//----------------------------------

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public boolean isOpenWindowInSameBorwserWindow() {
		return openWindowInSameBorwserWindow;
	}

	public void setOpenWindowInSameBorwserWindow(
			boolean openWindowInSameBorwserWindow) {
		this.openWindowInSameBorwserWindow = openWindowInSameBorwserWindow;
	}
	
	
	
	
	
	

}
