package com.zzn.pojie;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class HttpDownloadTest {
    @Autowired
    private CloseableHttpClient httpClient;

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE= Executors.newScheduledThreadPool(1);


    @Test
    public void getTest() throws IOException {
        HttpGet httpget = new HttpGet("http://erlang.org/download/otp_src_21.3.tar.gz");
        HttpResponse response = httpClient.execute(httpget);
        String name = response.getHeaders("Content-Length")[0].getElements()[0].getName();
        HttpEntity entity = response.getEntity();
        long contentLength = entity.getContentLength();
        InputStream is = entity.getContent();
        FileOutputStream fileout =null;
        DownloadThread downloadThread=new DownloadThread(entity.getContentLength());
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(downloadThread,1,1, TimeUnit.SECONDS);
        try {
            File file = new File("D:\\TOOL\\tools\\otp_src_21.3.tar.gz");
//        file.getParentFile().mkdirs();
            File parentFile = file.getParentFile();
            fileout=new FileOutputStream(file);

            byte[] buffer = new byte[1024*100];
            int len = 0;
            int count=0;
            while ((len = is.read(buffer)) != -1) {
                count++;
                downloadThread.downloadSize+=len;
                fileout.write(buffer, 0, len);
                if(count%100==0){
                    fileout.flush();
                }
            }
        } finally {
            if(is!=null){
                is.close();
            }
            if(fileout!=null){
                fileout.close();
            }
            System.out.print("\r");
            System.out.print("????????????");
            SCHEDULED_EXECUTOR_SERVICE.shutdownNow();
        }
    }
    private static final double MB=1024d*1024d;

    public class DownloadThread implements Runnable{
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

    @Test
    public void testSout(){
        System.out.print("\r");
        System.out.println("??????");
        System.out.print("\r");
        System.out.println("??????");
    }
}
