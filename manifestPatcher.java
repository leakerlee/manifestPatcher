package com.reverse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import com.apk.axml.aXMLDecoder;
import com.apk.axml.aXMLEncoder;

public class ManifestPatcher {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("用法: java -jar ManifestPatcher.jar <输入Manifest文件> <输出Manifest文件>");
            System.exit(1);
        }

        String inputFile = args[0];
        String outputFile = args[1];

        File inFile = new File(inputFile);
        if (!inFile.exists() || !inFile.isFile()) {
            System.out.println("[!] 输入文件不存在: " + inputFile);
            System.exit(1);
        }

        try {
            // 解码二进制 AXML 为可读的 XML 字符串
            aXMLDecoder decoder = new aXMLDecoder(new FileInputStream(inputFile));
            String xmlString = decoder.decodeAsString();

            // 检查是否已经为 debuggable
            if (xmlString.contains("android:debuggable=\"true\"")) {
                System.out.println("[!] 已存在 android:debuggable=\"true\"，跳过修改。");
                return;
            }

            // 简单替换：在 <application 标签中添加 debuggable 属性
            String modifiedXml = xmlString.replaceFirst("<application", "<application android:debuggable=\"true\"");

            // 重新编码为二进制 AXML
            aXMLEncoder encoder = new aXMLEncoder();
            byte[] outputBytes = encoder.encodeString(modifiedXml);

            // 写入文件
            try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                outputStream.write(outputBytes);
            }
            System.out.println("[+] 补丁成功！已保存到: " + outputFile);
        } catch (Exception e) {
            System.err.println("[!] 补丁过程中发生错误：");
            e.printStackTrace();
        }
    }
}
