package com.zzn.pojie;

import com.zzn.pojie.utils.BiliDownSchedul;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import sun.rmi.runtime.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

@Slf4j
@SpringBootTest
class PojieApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void testYouGet() {
        BiliDownSchedul.exeCmd("you-get https://www.bilibili.com/video/BV18E411W7q6?p=", 4, 23,"D:\\videos\\领域驱动设计\\3章");  //21
//        ConcurrentHashMap<Integer,String> concurrentHashMap=new ConcurrentHashMap<>();
//        concurrentHashMap.put(1,"");
//        concurrentHashMap.put(2,"");
//        ConcurrentHashMap.KeySetView<Integer, String> integers = concurrentHashMap.keySet();
//        log.info("ccc:{}",integers);
//        BiliDownSchedul.exeCmd("you-get https://www.bilibili.com/video/BV1dk4y1d7SS?p=", 2, 23,"D:\\videos\\领域驱动设计");  //21
//        exeCmd("you-get https://www.bilibili.com/video/BV13o4y1U78E?p=5",56,67);
    }



}
