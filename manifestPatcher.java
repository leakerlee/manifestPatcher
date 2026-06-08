package com.reverse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.xmlpull.v1.XmlPullParser;
import com.github.st4200.axml.AXMLDecoder; // 路径发生了变化
import com.github.st4200.axml.AXMLEncoder; // 路径发生了变化

public class ManifestPatcher {
    public static void main(String[] args) {
        // ... 前面的检查代码保持不变 ...
        try {
            // 解码
            AXMLDecoder decoder = AXMLDecoder.decode(new FileInputStream(inputFile));
            String xmlString = decoder.toString(); // 该库提供了直接转 String 的方法

            // 逻辑修改
            if (xmlString.contains("android:debuggable=\"true\"")) {
                System.out.println("[!] 已存在，跳过。");
                return;
            }

            String modifiedXml = xmlString.replaceFirst("<application", "<application android:debuggable=\"true\"");

            // 重新编码
            byte[] outputBytes = new AXMLEncoder().encode(modifiedXml);

            // 写入
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);
            outputStream.close();
            System.out.println("[+] 成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
