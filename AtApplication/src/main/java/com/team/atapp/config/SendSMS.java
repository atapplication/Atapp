/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team.atapp.config;


public interface SendSMS {

	public void send(String mobileNo, String messgae);
	public void send(String mobileNo[], String messgae);
	public void send(String mobileNo[], String message[]);
}
