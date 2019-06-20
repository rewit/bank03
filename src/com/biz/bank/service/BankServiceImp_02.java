package com.biz.bank.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import com.biz.bank.model.BankBalanceVO;

public class BankServiceImp_02 implements BankService {
	
	String accIolistpath = null;             
	String balanceFile = null;
	
	Map<String ,BankBalanceVO> balanceList = null;
	
	
	Scanner scan = null;
	FileReader fileReader = null;
	BufferedReader buffer = null;
	
	public BankServiceImp_02(String balanceFile) throws FileNotFoundException {
		
		
		this.balanceFile = balanceFile;
		
		accIolistpath = "src/com/biz/bank/iolist/";
		scan = new Scanner(System.in);
		/*
		 * Map 자료구조는 HashMap , LinkedHashMap, TreeMap가 있는데 
		 * key로 값을 검색하는 일이 많을 때는 TreeMap으로
		 * 초기화 하는것이 매우 효율적이다 
		 */
		balanceList = new TreeMap<String, BankBalanceVO>();
		
		fileReader = new FileReader(balanceFile);
		buffer = new BufferedReader(fileReader);
	}


	

	@Override
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
			//Map의 구조를 계좌번호를 key로 하고
			//값을 vo로 설정
			//Map에는 put() 메서드를 이용해서 List를 추가
			balanceList.put(banks[0], vo);  //Map은 key만 알면 추출 가능

		}
		
		buffer.close();
		fileReader.close();

	}
	
	

	@Override
	public void writeBalance() throws IOException {
		FileWriter fileWriter;
		PrintWriter printWriter;
		
		fileWriter = new FileWriter(balanceFile);
		printWriter = new PrintWriter(fileWriter);
		
		/*
		 * Map 구조에 저장된 List를 vo로 추출해서 사용하기 위해서는
		 * 먼저 Map구조에 있는 key들을 Set 구조로 추출
		 */
		
		Set<String> keyList = balanceList.keySet();
		
		/*
		 * 추출된 keySet을 for문으로 반복하면서
		 * map.get(key) 메서드를 호출해서 
		 * vo를 하나씩 추출한다
		 * 
		 * 그리고 알아서 요리한다
		 */
		
		for(String key : keyList) {
			BankBalanceVO vo = balanceList.get(key);
			printWriter.printf("%s:%d:%s\n",vo.getAcc(),vo.getBalance(),vo.getDate());
		}
		printWriter.flush();
		printWriter.close();
		
	}

	@Override
	public BankBalanceVO pickAcc(String accNum) {
		/*
		 * Map구조에 저장된 어떤 값을 찾을때는 
		 * key값만 알면 아주 단순한 코드로 값을 찾을수 있다
		 * 
		 * get(key)메서드는 Map자료에 해당하는 key가 있으면
		 * 해당하는 값을 return할 것이고 
		 * 그렇지 않으면 null을 리턴 할 것이다.
		 */
		return balanceList.get(accNum);
		//return null;
	}

	@Override
	public void inputMoney(String acc, int money) {

		BankBalanceVO vo = pickAcc(acc); 

		if(vo == null) {
			System.out.println("계좌번호가 없습니다");
			return;
		}
		int bal = vo.getBalance();
		vo.setBalance(bal + money);
		
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
		
			fileWriter = new FileWriter(accIolistpath +"KBANK_"+ accNum,true);
			printWriter = new PrintWriter(fileWriter);
			printWriter.printf("%s:%s:%d:%d:%d\n", vo.getDate(),"입금",money,0,vo.getBalance() );
			printWriter.flush();  
			printWriter.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
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
	
	

	@Override
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
