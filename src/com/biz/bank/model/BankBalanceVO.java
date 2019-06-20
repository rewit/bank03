package com.biz.bank.model;

public class BankBalanceVO {
	// 1. 클래스 생성
	
	// 2. 필드 생성
	private String acc;
	private int balance;
	private String date;

	
	// 3. getter & setter , toString, 기본생성자, 필드생성자 작성 
	
	public BankBalanceVO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public BankBalanceVO(String acc, int balance, String date) {
		super();
		this.acc = acc;
		this.balance = balance;
		this.date = date;
	}
	
	public String getAcc() {
		return acc;
	}
	public void setAcc(String acc) {
		this.acc = acc;
	}
	public int getBalance() {
		return balance;
	}
	public void setBalance(int balance) {
		this.balance = balance;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	@Override
	public String toString() {
		return "BankBalanceVO [acc=" + acc + ", balance=" + balance + ", date=" + date + "]";
	}
	
}
