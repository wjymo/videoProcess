package com.zzn.pojie;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.zzn.pojie.model.InputOutputFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;


@SpringBootTest
class SaveBiliVideoTests {

    private static final Map<String, List<String>> MAP = new HashMap();
    private static final Table<String, String, InputOutputFile> universityCourseSeatTable
            = HashBasedTable.create();

    @Test
    void contextLoads() {
    }

    @Test
    public void testBilVideo() throws IOException {
//        saveVideo("D:\\videos\\blib\\79567276\\7\\80\\video.m4s",
//                "D:\\videos\\blib\\79567276\\7\\80\\audio.m4s","D:\\videos\\blib\\my\\xx.mp4");
//        File root = new File("D:\\videos\\blib");
        List<File> roots = new ArrayList<>();
        File[] files = new File("E:\\videos2\\压缩bili").listFiles();
        roots.addAll(Arrays.asList(files));
//        roots.add(new File("D:\\videos\\blib\\842005319"));

        save2Local(roots);
    }



    public void save2Local(List<File> roots) throws IOException {
        for (File root : roots) {
            File[] twoLevelFiles = root.listFiles();
            boolean hasSome = false;
            if (twoLevelFiles.length > 1) {
                hasSome = true;
            }
            for (File twoLevelFile : twoLevelFiles) {
                String pageName = twoLevelFile.getName();
                File[] threeLevelFiles = twoLevelFile.listFiles();
                InputOutputFile inputOutputFile = new InputOutputFile();
                for (File threeLevelFile : threeLevelFiles) {
                    String title = null;
                    String part = null;
                    String threeLevelFileName = threeLevelFile.getName();
                    if (StringUtils.equals(threeLevelFileName, "entry.json")) {
                        String s = FileUtils.readFileToString(threeLevelFile, "utf-8");
                        JSONObject jsonObject = JSON.parseObject(s);
                        title = jsonObject.getString("title");
                        title=StringUtils.replace(title," ","");
                        JSONObject page_data = jsonObject.getJSONObject("page_data");
                        part = page_data.getString("part");
                        part=StringUtils.replace(part," ","");
                        File outputDir = new File("D:\\videos\\" + title);
                        if (!outputDir.exists()) {
                            outputDir.mkdir();
                        }
                        if (hasSome) {
                            if (pageName.length() == 1) {
                                pageName = "0" + pageName;
                            }
                            inputOutputFile.setOutput("D:\\videos\\" + title + "\\" + pageName + "-" + part + ".mp4");
                        } else {
                            inputOutputFile.setOutput("D:\\videos\\" + title + "\\" + part + ".mp4");
                        }
                        universityCourseSeatTable.put(title, part, inputOutputFile);
                    } else {
                        if (threeLevelFile.isDirectory()) {
                            File[] m4sFiles = threeLevelFile.listFiles();
                            for (File m4sFile : m4sFiles) {
                                if (!StringUtils.endsWith(m4sFile.getName(), "json")) {
                                    String absolutePath = m4sFile.getAbsolutePath();
//                                    System.out.println(absolutePath);
                                    if (StringUtils.startsWith(m4sFile.getName(), "audio")) {
                                        inputOutputFile.setInputAudio(absolutePath);
                                    } else if (StringUtils.startsWith(m4sFile.getName(), "video")) {
                                        inputOutputFile.setInputVideo(absolutePath);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Map<String, Map<String, InputOutputFile>> stringMapMap = universityCourseSeatTable.columnMap();
//        System.out.println(JSON.toJSONString(stringMapMap));
//        System.out.println(1);
        for (Map.Entry<String, Map<String, InputOutputFile>> stringMapEntry : stringMapMap.entrySet()) {
            Map<String, InputOutputFile> value = stringMapEntry.getValue();
            for (Map.Entry<String, InputOutputFile> stringInputOutputFileEntry : value.entrySet()) {
                InputOutputFile inputOutputFile = stringInputOutputFileEntry.getValue();
                saveVideo(inputOutputFile.getInputVideo(), inputOutputFile.getInputAudio(), inputOutputFile.getOutput());
            }
        }
    }


    public static void saveVideo(String videoM4s, String audioM4s, String output) {
        String command = "D:\\otherTools\\ffmpeg\\ffmpeg-4.4-essentials_build\\bin\\ffmpeg -i %s -i %s -codec copy %s";
        String format = String.format(command, videoM4s, audioM4s, output);
        System.out.println(format);
        AtomicReference<BufferedReader> br = new AtomicReference<>();
        try {
            Process p = Runtime.getRuntime().exec(format);
            br.set(new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.forName("utf-8"))));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.get().readLine()) != null) {
                sb.append(line + "\n");
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                if (br.get() != null) {
                    try {
                        br.get().close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @Test
    public void deleteXml(){
        File file=new File("D:\\videos\\监控 - 副本");
        for (File listFile : file.listFiles()) {
            String name = listFile.getName();
            if(StringUtils.endsWith(name,"xml")){
                boolean delete = listFile.delete();
                System.out.println(delete);
            }
        }
    }
}
