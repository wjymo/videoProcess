package com.zzn.pojie.utils;

import java.io.*;
import java.util.Scanner;

public class DictionarySeek {
    //密码可能会包含的字符集合
    private static char[] fullCharSource = { '1','2','3','4','5','6','7','8','9','0',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',  'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',  'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '~', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '_', '+', '{', '}', '|', ':', '"', '<', '>', '?', ';', '\'', ',', '.', '/', '-', '=', '`'};
    //将可能的密码集合长度
    private static int fullCharLength = fullCharSource.length;
    //maxLength：生成的字符串的最大长度
    public static void generate(int maxLength) throws FileNotFoundException, UnsupportedEncodingException {
        //计数器，多线程时可以对其加锁，当然得先转换成Integer类型。
        int counter = 0;
        StringBuilder buider = new StringBuilder();
        
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("D://密码字典.txt"), "utf-8"));

        while (buider.toString().length() <= maxLength) {
            buider = new StringBuilder(maxLength*2);
            int _counter = counter;
            //10进制转换成26进制
            while (_counter >= fullCharLength) {
                //获得低位
                buider.insert(0, fullCharSource[_counter % fullCharLength]);
                _counter = _counter / fullCharLength;
                //处理进制体系中只有10没有01的问题，在穷举里面是可以存在01的
                _counter--;
            }
            //最高位
            buider.insert(0,fullCharSource[_counter]);
            counter++;
            
            pw.write(buider.toString()+"\n");
            System.out.println(buider.toString());
        }
    }
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
    	System.out.print("生成的字典位置：D://密码字典.txt"+"\n"+"请输入你需要生成的字典位数：");
    	Scanner sc = new Scanner(System.in);
    	int x = sc.nextInt();
    	
    	DictionarySeek.generate(x);
	
	}
}