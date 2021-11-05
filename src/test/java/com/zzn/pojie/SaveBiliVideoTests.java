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

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;


@SpringBootTest
class SaveBiliVideoTests {

    private static final Map<String, List<String>> MAP = new HashMap();
    private static final Table<String, String, InputOutputFile> universityCourseSeatTable
            = HashBasedTable.create();

    private static final List<String> parts = new ArrayList<>();

    @Test
    void contextLoads() {
    }

    @Test
    public void testBilVideo() throws IOException {
//        saveVideo("D:\\videos\\blib\\79567276\\7\\80\\video.m4s",
//                "D:\\videos\\blib\\79567276\\7\\80\\audio.m4s","D:\\videos\\blib\\my\\xx.mp4");
//        File root = new File("D:\\videos\\blib");
        List<File> roots = new ArrayList<>();
        //以config_db开头，文件后缀为.properties的将被选出来，其余被过滤掉
//        File[] files = new File("E:\\videos2\\压缩bili").listFiles((dir, name) -> {
//            if(StringUtils.equals(name,"848712363"))
//                return false;
//            return true;
//        });
//        roots.addAll(Arrays.asList(files));
        roots.add(new File("E:\\videos2\\压缩bili\\89905283"));

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
                if (StringUtils.equals("c_427311169", pageName)
                        || StringUtils.equals("c_427312266", pageName) || StringUtils.equals("c_427313246", pageName)
                        || StringUtils.equals("c_427313263", pageName)) {
                    continue;
                }
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
                        title = StringUtils.replace(title, " ", "");
                        title = StringUtils.replace(title, "/", "-");
                        JSONObject page_data = jsonObject.getJSONObject("page_data");
                        part = page_data.getString("part");
                        if (part != null) {
                            part = StringUtils.replace(part, " ", "");
                            File outputDir = new File("E:\\videos2\\哔哩下载\\" + title);
                            if (!outputDir.exists()) {
                                outputDir.mkdir();
                            }
                        }
                        parts.add(part);
                        if (hasSome) {
                            if (pageName.length() == 1) {
                                pageName = "0" + pageName;
                            }
                            pageName = "b";
                            if (StringUtils.isEmpty(part)) {
                                inputOutputFile.setOutput("E:\\videos2\\哔哩下载\\" + title + "\\" + pageName + ".mp4");
                            } else {
                                inputOutputFile.setOutput("E:\\videos2\\哔哩下载\\" + title + "\\" + pageName + "-" + part + ".mp4");
                            }
                        } else {
                            if (StringUtils.isEmpty(part)) {
                                inputOutputFile.setOutput("E:\\videos2\\哔哩下载\\" + title + ".mp4");
                            } else {
                                inputOutputFile.setOutput("E:\\videos2\\哔哩下载\\" + title + "\\" + part + ".mp4");
                            }
                        }
                        if (part == null) {
                            part = "-";
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
            while ((line = br.get().readLine()) != null && line.length() > 0) {
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
    public void deleteXml() {
        File file = new File("D:\\videos\\监控 - 副本");
        for (File listFile : file.listFiles()) {
            String name = listFile.getName();
            if (StringUtils.endsWith(name, "xml")) {
                boolean delete = listFile.delete();
                System.out.println(delete);
            }
        }
    }

    @Test
    public void clearPrefx() {
        File root = new File("E:\\videos2\\哔哩下载\\阿里云云计算运维与服务器ECS管理工程师（完）");
        File[] files = root.listFiles();
        for (File file : files) {
            transportFileName(file);
        }
    }

    private void transportFileName(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File fileInFor : files) {
                transportFileName(fileInFor);
            }
        } else {
            String name = file.getName();
            String absolutePath = file.getAbsolutePath();
            String absoluteRootPath = StringUtils.substring(absolutePath, 0, absolutePath.lastIndexOf("\\"));
            String[] split = name.split("-");
            String realName = split[1];
            File newFile = new File(absoluteRootPath, realName);
            boolean b = file.renameTo(newFile);
            System.out.println(b);
        }
    }
}
