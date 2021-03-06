package com.zzn.pojie;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class PojieApplication {

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE= Executors.newScheduledThreadPool(1);
    private final static LongAdder LONG_ADDER=new LongAdder();
    private static HttpResponse xunhuan(CloseableHttpClient httpClient,HttpGet httpget){
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpget);
        } catch (IOException e) {
            log.error(e.getMessage(),e);
            LONG_ADDER.increment();
            long l = LONG_ADDER.longValue();
            if(Objects.equals(l,20)){
                System.exit(1);
            }
            return xunhuan(httpClient,httpget);
        }
        return response;
    }
    public static void main(String[] args) throws IOException {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(PojieApplication.class, args);

//        CloseableHttpClient httpClient = applicationContext.getBean(CloseableHttpClient.class);
//
////        HttpGet httpget = new HttpGet("https://github.com/containernetworking/plugins/releases/download/v0.8.6/cni-plugins-linux-amd64-v0.8.6.tgz");
//        HttpGet httpget = new HttpGet("https://github.com/containernetworking/plugins/releases/download/v0.8.6/cni-plugins-linux-amd64-v0.8.6.tgz");
//        HttpResponse response = null;
//        response = xunhuan(httpClient, httpget);
//        String name = response.getHeaders("Content-Length")[0].getElements()[0].getName();
//        HttpEntity entity = response.getEntity();
//        long contentLength = entity.getContentLength();
//        InputStream is = entity.getContent();
//        FileOutputStream fileout =null;
//        DownloadThread downloadThread=new DownloadThread(entity.getContentLength());
//        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(downloadThread,1,1, TimeUnit.SECONDS);
//        try {
//            File file = new File("D:\\TOOL\\tools\\cni-plugins-linux-amd64-v0.8.6.tgz");
////        file.getParentFile().mkdirs();
//            File parentFile = file.getParentFile();
//            if(!parentFile.exists()){
//                parentFile.mkdirs();
//            }
//            fileout=new FileOutputStream(file);
//
//            byte[] buffer = new byte[1024*100];
//            int len = 0;
//            int count=0;
//            while ((len = is.read(buffer)) != -1) {
//                count++;
//                downloadThread.downloadSize+=len;
//                fileout.write(buffer, 0, len);
//                if(count%100==0){
//                    fileout.flush();
//                }
//            }
//        } finally {
//            if(is!=null){
//                is.close();
//            }
//            if(fileout!=null){
//                fileout.close();
//            }
//            System.out.print("\r");
//            System.out.print("????????????");
//            SCHEDULED_EXECUTOR_SERVICE.shutdownNow();
//            System.exit(0);
//        }
    }

    private static final double MB=1024d*1024d;
    public static class DownloadThread implements Runnable{
        //?????????????????????
        private long httpFileContentLength;

        //???????????????????????????
        private double localFinishedSize;

        //????????????????????????
        private double prevSize;

        //??????????????????????????????
        public volatile double  downloadSize;

        public DownloadThread(long httpFileContentLength) {
            this.httpFileContentLength = httpFileContentLength;
        }

        @Override
        public void run() {
            String contentLengthFormat = String.format("%.2f", httpFileContentLength / MB);

            //?????????????????????kb
            int speed=(int)((downloadSize-prevSize)/1024d);
            prevSize=downloadSize;

            //??????????????????
            double remainSize = httpFileContentLength - localFinishedSize - downloadSize;

            //??????????????????
            String remainTime = String.format("%.1f",remainSize / 1024d / speed) ;
            if(StringUtils.equalsIgnoreCase(remainTime,"Infinity")){
                remainTime="-";
            }
            //???????????????
            String currentFileSize = String.format("%.2f", (downloadSize - localFinishedSize) / MB);

            String downloafInfo=String.format("?????????:%smb ??????:%skb/s ????????????:%ss",currentFileSize,speed,remainTime);

            System.out.print("\r");
            System.out.print(downloafInfo);
        }
    }

}
