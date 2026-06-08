package com.reverse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

// 具体的类名可能因 aXML 版本不同略有差异，通常如下：
import com.apk.editor.axml.AXmlDecoder;
import com.apk.editor.axml.AXmlEncoder;

public class ManifestPatcher {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("使用说明: java -jar ManifestPatcher.jar <输入的AndroidManifest.xml> <输出的AndroidManifest.xml>");
            System.exit(1);
        }

        File inputFile = new File(args[0]);
        File outputFile = new File(args[1]);

        if (!inputFile.exists()) {
            System.out.println("错误: 找不到输入文件 -> " + inputFile.getAbsolutePath());
            System.exit(1);
        }

        try {
            System.out.println("[*] 开始解析二进制 XML...");
            
            // 1. 读取并解码二进制 XML 为普通文本字符串
            InputStream inputStream = new FileInputStream(inputFile);
            AXmlDecoder decoder = new AXmlDecoder(inputStream);
            String xmlString = decoder.decode();
            inputStream.close();

            // 2. 检查是否已经存在 debuggable 属性
            if (xmlString.contains("android:debuggable=\"true\"")) {
                System.out.println("[!] 该文件已经包含 debuggable=\"true\"，跳过修改。");
                return;
            }

            System.out.println("[*] 正在注入 debuggable 属性...");
            
            // 3. 暴力且优雅的字符串替换：找到 <application 并插入属性
            // 注意：这里需要带上一个空格，防止和原有属性连在一起
            String modifiedXml = xmlString.replaceFirst(
                    "<application", 
                    "<application android:debuggable=\"true\""
            );

            // 4. 将修改后的纯文本重新编码为二进制格式
            System.out.println("[*] 正在重编译为二进制 XML...");
            AXmlEncoder encoder = new AXmlEncoder();
            byte[] outputBytes = encoder.encode(modifiedXml);

            // 5. 写入到输出文件
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);
            outputStream.close();

            System.out.println("[+] 成功！已生成修改后的文件: " + outputFile.getAbsolutePath());

        } catch (Exception e) {
            System.out.println("[-] 发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
