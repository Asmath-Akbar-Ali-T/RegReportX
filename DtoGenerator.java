import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DtoGenerator {
    public static void main(String[] args) throws Exception {
        String baseDir = "src/main/java/com/cts/regreportx";
        File modelDir = new File(baseDir + "/model");
        File dtoDir = new File(baseDir + "/dto");
        
        if (!dtoDir.exists()) {
            dtoDir.mkdirs();
        }

        File[] files = modelDir.listFiles((dir, name) -> name.endsWith(".java"));
        if (files == null) return;

        int count = 0;
        for (File file : files) {
            String className = file.getName().replace(".java", "");
            String dtoClassName = className + "Dto";
            
            List<String> lines = Files.readAllLines(file.toPath());
            List<String> dtoLines = new ArrayList<>();
            
            for (String line : lines) {
                // Skip JPA annotations
                if (line.contains("@Entity") || line.contains("@Table") || 
                    line.contains("@Id") || line.contains("@GeneratedValue") || 
                    line.contains("@Column") || line.contains("@ManyToOne") ||
                    line.contains("@OneToMany") || line.contains("@JoinColumn") || 
                    line.contains("@Transient")) {
                    continue;
                }
                
                // Remove jakarta.persistence imports
                if (line.contains("import jakarta.persistence")) {
                    continue;
                }
                
                // Change package
                if (line.startsWith("package ")) {
                    line = "package com.cts.regreportx.dto;";
                }
                
                // Change class declaration
                line = line.replace("public class " + className, "public class " + dtoClassName);
                
                // Change constructor
                line = line.replace("public " + className + "(", "public " + dtoClassName + "(");
                
                dtoLines.add(line);
            }
            
            Path outputPath = Paths.get(dtoDir.getAbsolutePath(), dtoClassName + ".java");
            Files.write(outputPath, dtoLines);
            count++;
        }
        System.out.println("Generated " + count + " DTOs successfully.");
    }
}
