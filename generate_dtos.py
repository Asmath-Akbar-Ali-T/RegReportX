import os
import re

MODEL_DIR = r"c:\Users\janan\Downloads\RegReportX\src\main\java\com\cts\regreportx\model"
DTO_DIR = r"c:\Users\janan\Downloads\RegReportX\src\main\java\com\cts\regreportx\dto"

if not os.path.exists(DTO_DIR):
    os.makedirs(DTO_DIR)

for filename in os.listdir(MODEL_DIR):
    if filename.endswith(".java"):
        class_name = filename[:-5]
        dto_class_name = f"{class_name}Dto"
        
        with open(os.path.join(MODEL_DIR, filename), "r", encoding="utf-8") as f:
            lines = f.readlines()
            
        dto_lines = []
        for line in lines:
            # Skip JPA annotations
            if "@Entity" in line or "@Table" in line or "@Id" in line or "@GeneratedValue" in line or "@Column" in line:
                continue
            
            # Remove jakarta.persistence imports
            if "import jakarta.persistence" in line:
                continue
                
            # Change package
            if line.startswith("package com.cts.regreportx.model;"):
                line = "package com.cts.regreportx.dto;\n"
                
            # Change class declaration
            class_decl_match = re.search(r"public\s+class\s+([A-Za-z0-9_]+)", line)
            if class_decl_match:
                found_name = class_decl_match.group(1)
                line = line.replace(f"class {found_name}", f"class {dto_class_name}")
                
            # Change constructor
            if f"public {class_name}(" in line:
                line = line.replace(f"public {class_name}(", f"public {dto_class_name}(")
                
            dto_lines.append(line)
            
        with open(os.path.join(DTO_DIR, f"{dto_class_name}.java"), "w", encoding="utf-8") as f:
            f.writelines(dto_lines)
            
print("Generated 22 DTOs successfully.")
