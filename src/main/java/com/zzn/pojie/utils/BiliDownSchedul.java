package com.zzn.pojie.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;
@Slf4j
@Component
public class BiliDownSchedul {

    private static final ConcurrentHashMap<Integer,String> CONCURRENT_HASH_MAP=new ConcurrentHashMap<>();
    //cron转换网址：https://cron.qqe2.com/

    @Scheduled(cron = "0 0 18 * * ?")
    public void exeYouGetCmd(){
        System.out.println("开始执行bili任务，现在时间："+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        exeCmd("you-get https://www.bilibili.com/video/BV1jQ4y1Q76F?p=", 3, 63,"E:\\videos2\\李运华架构营");
        System.out.println("执行bili任务完成，现在时间："+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

    public static void exeCmd(String commandStr, int from, int to,String output) {
        AtomicReference<BufferedReader> br = new AtomicReference<>();
        try {
            IntStream.range(from, to).forEach(i -> {
                try {
                    Process p = Runtime.getRuntime().exec(commandStr + i+" -o "+output);
                    br.set(new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.forName("utf-8"))));
                    String line = null;
                    StringBuilder sb = new StringBuilder();
                    while ((line = br.get().readLine()) != null) {
                        sb.append(line + "\n");
                        System.out.println(line);
                    }
                } catch (Exception e) {
                    log.error("获取第：{}个视频失败，异常：{}",i,e.getMessage(),e);
                    CONCURRENT_HASH_MAP.put(i,"");
                }
            });
        } finally {
            if(br!=null){
                if (br.get() != null) {
                    try {
                        br.get().close();
                    } catch (Exception e) {
                        log.error("finally里关闭流发生异常：{}",e.getMessage(),e);
                    }
                }
            }
            ConcurrentHashMap.KeySetView<Integer, String> integers = CONCURRENT_HASH_MAP.keySet();
            log.info("【最后】这些序号的视频下载失败：{}",integers);
        }
    }
}
