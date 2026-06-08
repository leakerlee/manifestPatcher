package com.reverse;

import net.dongliu.apk.parser.ApkFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class ManifestPatcher {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("用法: java -jar ManifestPatcher.jar <输入Manifest文件> <输出Manifest文件>");
            System.out.println("示例: java -jar ManifestPatcher.jar AndroidManifest.xml patched_AndroidManifest.xml");
            System.exit(1);
        }

        String inputPath = args[0];
        String outputPath = args[1];

        File inputFile = new File(inputPath);
        if (!inputFile.exists() || !inputFile.isFile()) {
            System.out.println("[!] 输入文件不存在: " + inputPath);
            System.exit(1);
        }

        try (ApkFile apkFile = new ApkFile(inputFile)) {
            // 获取解码后的 Manifest XML 字符串
            String xmlString = apkFile.getManifestXml();

            if (xmlString.contains("android:debuggable=\"true\"")) {
                System.out.println("[!] 已存在 android:debuggable=\"true\"，跳过修改。");
                // 直接复制原文件
                Files.copy(inputFile.toPath(), new File(outputPath).toPath());
                return;
            }

            // 更安全的正则替换方式（避免格式问题）
            String modifiedXml = xmlString.replaceFirst(
                "(<application[^>]*?)(/?>)", 
                "$1 android:debuggable=\"true\"$2"
            );

            System.out.println("[+] 已成功添加 android:debuggable=\"true\"");

            // 注意：apk-parser 主要用于解析，重新生成二进制 AXML 需要编码器
            // 这里先输出修改后的 XML 文本（开发者常用方式）
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                fos.write(modifiedXml.getBytes("UTF-8"));
            }

            System.out.println("[+] 补丁完成！输出文件: " + outputPath);
            System.out.println("   （输出的是 XML 文本格式，可用于进一步处理）");

        } catch (Exception e) {
            System.err.println("[!] 处理过程中发生错误：");
            e.printStackTrace();
        }
    }
}
