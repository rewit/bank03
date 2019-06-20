package com.biz.bank.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import com.biz.bank.model.BankBalanceVO;

public class BankServiceImp_03 implements BankService {

	String accIolistpath = null;              //선언하고  null로 임시로 클리어  안넣어도 쓰는데 문제는 없음
	String balanceFile = null;
	List<BankBalanceVO> balanceList = null;
	Scanner scan = null;
	FileReader fileReader = null;
	BufferedReader buffer = null;

	public BankServiceImp_03(String balanceFile) throws FileNotFoundException {
	
		/*
		 * balanceFile 이름을 필드에 있는
		 * this.balanceFile 에 저장하여 
		 * 클래스 내에서 자유롭게 접근할 수 있도록 한다.
		 * 
		 * 같은 이름을 못쓰는 언어는 앞에 _ 를 붙인다
		 */
		
		this.balanceFile = balanceFile;
		//매개변수로 받은 값을 this 파일에 저장 (쓸 수 있도록 준비)
		
		accIolistpath = "src/com/biz/bank/iolist/";
		//iolist를 저장할 폴더 선언
		scan = new Scanner(System.in);
		balanceList = new ArrayList<BankBalanceVO>();
		fileReader = new FileReader(balanceFile);
		buffer = new BufferedReader(fileReader);
		// 생성자에서 생성해야 다른곳에서 오류가 덜 함
	}

	/*
	 * balance.txt 파일을 읽어서 계좌정보를 
	 * balanceList에 추가하는 메서드를 작성
	 */

	public void readBalance() throws IOException {
		// reader 생성
		// 반복위해 while문

		String reader = "";
		while (true) {
			reader = buffer.readLine();
			if (reader == null)
				break;

			String[] banks = reader.split(":");

			BankBalanceVO vo = new BankBalanceVO(banks[0], Integer.valueOf(banks[1]), banks[2]);

			vo.setAcc(banks[0]);
			vo.setBalance(Integer.valueOf(banks[1]));
			vo.setDate(banks[2]);
			
			balanceList.add(vo); 

		}
		//balance.txt 파일을 처음 한번 읽어서
		//balanceList에 담고나면
		//buffer와 fileReader는 하는 일이 끝나므로
		//두 객체 모두 close() 실행을 한다
		// - 프로젝트가 종료될때
		// 	 balance.txt 파일에 내용을 기록해야하는데
		// 	 reader 상태로 열려있으면
		//	 기록이 잘 안되는 경우가 있기 때문에
		
		buffer.close();
		fileReader.close();

	}
	
	
	/*
	 *이 메서드는 프로젝트가 종료되기 직전에 실행되어서 
	 *balanceList에 담긴 내용을 balance.txt에 몽땅 기록한다 
	 */
	public void  writeBalance() throws IOException {
		
		FileWriter fileWriter;
		PrintWriter printWriter;
		
		fileWriter = new FileWriter(balanceFile);
		printWriter = new PrintWriter(fileWriter);
		
		for(BankBalanceVO vo : balanceList) {
			printWriter.printf("%s:%d:%s\n",vo.getAcc(),vo.getBalance(),vo.getDate());
		}
		printWriter.flush();
		printWriter.close();
		
		
	}
	
	

	public BankBalanceVO pickAcc(String accNum) {

		//String acc = "0001";
		/*
		 * balanceList에서 계좌번호 0001인 데이터를 찾고 그 계좌에 현잔액을 콘솔에 표시
		 */
		int index = 0;
		int bankSize = balanceList.size();
		BankBalanceVO vo = null;
////		for(BankBalanceVO vo : balanceList)	{		//for 안에 if는 1번만  없을 경우 확인은 for 밖에서
		for (index = 0; index < bankSize; index++) {
			vo = balanceList.get(index);
			if (vo.getAcc().equals(accNum)) {
				// System.out.println(vo.getBalance());
				return vo;
			}
		}
		return null;
	}//pickAcc end
	
	/*
	 * 계좌번호로 pickAcc()로부터 vo값을 추출해오고
	 * balance값과 money값을 더하여 
	 * vo의 balance에 저장하고 콘솔에 보여주는 코드
	 */
	public void inputMoney(String acc, int money) {
		
		BankBalanceVO vo = pickAcc(acc); //pickAcc한테 acc를 뱅크 밸런스 vo에 받음
		
		/*
		 *vo 값이 null인 경우는 다음코드로 진행하지 못하도록하며
		 *그 전에 사용자에게 메시지를 보여주어야 한다 
		 */
		
		if(vo == null) {
			System.out.println("계좌번호가 없습니다");
			return;
		}
		int bal = vo.getBalance();
		vo.setBalance(bal + money);
		
		//현재 컴퓨터날짜값을 가져오기
		//java 1.7 이하에서 지금도 사용하는 코드
		Date date = 
				new Date(System.currentTimeMillis()); //1.8이상에선 사용하지 x , 현 업체들은 1.7버전이라 많이 사용
							//1.8이상에서는 System.currentTimeMillis() 안넣어두됨 
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		
		String curDate = sf.format(date);
		
		vo.setDate(curDate);
		
		//Java 1.8(8)이상에서 사용하는 새로운 날짜
		LocalDate localDate = LocalDate.now();
		vo.setDate(localDate.toString()); 	//실무에선 아직 안씀
		
		//입금이 잘 되었나를 콘솔에 확인 시켜주는 부분
		System.out.println("======================================");
		System.out.println(vo);
		System.out.println("======================================");
	
		FileWriter fileWriter;
		PrintWriter printWriter;
		
		//계좌번호를 임시변수에 대입(저장)
		String accNum = vo.getAcc();
		
		
		// 입출금 거래내역을 개인통장에 기록
		try {
			
			//create mode:
			//fileWriter로 파일을 기록하기 위해서 실행하면
			//기존에 같은 이름의 파일이 있으면
			//삭제하고 새로 생성한다
			
			//append mode:
			//만약 기존에 파일 내용을 유지하면서
			//파일의 끝에 새로운 내용을 추가로 저장하고싶으면
			//new fileWriter()생성자 끝에 true 옵션을 추가한다.
		
			fileWriter = new FileWriter(accIolistpath +"KBANK_"+ accNum,true);//accIolistpath에 계좌번호를 가지고 파일을 생성
			printWriter = new PrintWriter(fileWriter);			//저장할 폴더 지정
			//파일에 내용을 기록하는 부분
			printWriter.printf("%s:%s:%d:%d:%d\n", vo.getDate(),"입금",money,0,vo.getBalance() );
			//printWriter은 반드시 닫아줘야함
			printWriter.flush();  
			printWriter.close();
			
			//fileWriter을 쓸때 똑같은 이름을 쓰면 기존에 있던것은 삭제가 됨 
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public void outputMoney(String acc, int money) {
		
		BankBalanceVO vo = pickAcc(acc);
		if(vo == null) {
			System.out.println("계좌번호가 없습니다");
			return;
		}
		int bal = vo.getBalance();

		// 출금일경우는 현잔액을 검사해서
		// 출금액보다 크면 출금 금지
		if(bal < money) {
			System.out.println("잔액부족!!!");
			return ;
		}
		vo.setBalance(bal - money);

		// java 1.7 이하에서 지금도 사용하는 코드
		// 현재 컴퓨터날짜값을 가져오기
		Date date 
		= new Date(System.currentTimeMillis());
		
		SimpleDateFormat sf 
			= new SimpleDateFormat("yyyy-MM-dd");
		
		String curDate = sf.format(date);
		vo.setDate(curDate);
		
		// java 1.8(8) 이상에서 사용하는 새로운 날짜
		LocalDate localDate = LocalDate.now();
		vo.setDate(localDate.toString());
		
		System.out.println("============================");
		System.out.println(vo);
		System.out.println("============================");
		
		FileWriter fileWriter;
		PrintWriter printWriter;
		
		String accNum = vo.getAcc();
		
		/*
		 * 출금 거래내역을 개인통장에 기록
		 */
		try {
			fileWriter = new FileWriter(accIolistpath 
							+ "KBANK_" + accNum,true);
			printWriter = new PrintWriter(fileWriter);
			
			// 파일에 내용을 기록하는 부분
			printWriter.printf("%s:%s:%d:%d:%d\n",
					vo.getDate(),
					"출금",
					0,
					money,
					vo.getBalance());
			
			printWriter.flush();
			printWriter.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int selectMenu() {
	System.out.println("===============================");
	System.out.println("1. 입금   2.출금   -9.종료");
	System.out.println("-------------------------------");
	System.out.print("업무선택");
	String strMenu = scan.nextLine();
	
	int intMenu = 0;
	try {
		intMenu = Integer.valueOf(strMenu);
	} catch (Exception e) {
		//오류 무시
	}
	return intMenu;
	
	}

}
