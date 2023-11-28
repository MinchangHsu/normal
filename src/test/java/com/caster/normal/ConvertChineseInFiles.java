package com.caster.normal; /**
 * @author caster.hsu
 * @Since 2023/11/21
 */
import com.github.houbb.opencc4j.util.ZhConverterUtil;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;

public class ConvertChineseInFiles {

    public static void main(String[] args) {
        // 輸入資料夾路徑
        String inputFolderPath = "C:\\Users\\caster.hsu\\Documents\\workSpace\\privateGithub\\KubernetesGitBook";
        // 輸出資料夾路徑
        String outputFolderPath = "C:\\Users\\caster.hsu\\Documents\\workFile\\temp\\k8sGitBookTemp\\new";

        // 進行簡繁轉換，並刪除檔名包含 '()' 的檔案，排除 'abc' 資料夾
        convertChineseInFolder(inputFolderPath, outputFolderPath);
    }

    private static void convertChineseInFolder(String inputFolderPath, String outputFolderPath) {
        try {
            Files.walkFileTree(Paths.get(inputFolderPath), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    // 檢查資料夾名稱是否包含
                    if (Arrays.asList(".git", ".gitbook", ".idea").contains(dir.getFileName().toString())) {
                        // 如果包含 'abc' 就跳過這個資料夾
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    // 檢查檔名是否包含 '()'
                    if (file.getFileName().toString().contains("()")) {
                        // 如果包含 '()' 就刪除檔案
                        Files.delete(file);
                        System.out.println("Deleted file: " + file);
                        return FileVisitResult.CONTINUE;
                    }

                    // 獲取相對路徑
                    Path relativePath = Paths.get(inputFolderPath).relativize(file);
                    // 輸出檔案路徑
                    Path outputPath = Paths.get(outputFolderPath, relativePath.toString());

                    // 確保輸出檔案的目錄存在
                    Files.createDirectories(outputPath.getParent());

                    // 如果是目錄，只需複製結構，不需要轉換內容
                    if (Files.isDirectory(file)) {
                        return FileVisitResult.CONTINUE;
                    }

                    // 獲取檔案內容
                    String fileContent = new String(Files.readAllBytes(file));
                    // 將中文轉換成繁體中文
                    String convertedContent = ZhConverterUtil.toTraditional(fileContent);

                    // 將轉換後的內容寫入輸出檔案
                    Files.write(outputPath, convertedContent.getBytes());

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    // 訪問檔案失敗時的處理邏輯
                    System.out.println("Failed to visit file: " + file);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}





