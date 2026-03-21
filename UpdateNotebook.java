import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UpdateNotebook {
    public static void main(String[] args) {
        try {
            Path notebookPath = Paths.get("src/main/resources/static/Dataset.ipynb");
            String content = new String(Files.readAllBytes(notebookPath));
            
            content = content.replace(
                "\"LoanAmount\": round(random.uniform(50000,10000000),2),",
                "\"LoanAmount\": round(random.uniform(50000,10000000),2) if random.random() > 0.05 else round(random.uniform(-10000, -100),2),"
            );
            
            content = content.replace(
                "\"InterestRate\": round(random.uniform(6.5,14),2),",
                "\"InterestRate\": round(random.uniform(6.5,14),2) if random.random() > 0.05 else round(random.uniform(21, 30),2),"
            );
            
            content = content.replace(
                "\"Amount\": round(random.uniform(1000,5000000),2),",
                "\"Amount\": round(random.uniform(1000,5000000),2) if random.random() > 0.05 else round(random.uniform(-5000, -100),2),"
            );
            
            content = content.replace(
                "debit = round(random.uniform(1000,500000),2)",
                "debit = round(random.uniform(1000,500000),2) if random.random() > 0.05 else -1000.0"
            );
            
            content = content.replace(
                "credit = round(random.uniform(1000,500000),2)",
                "credit = round(random.uniform(1000,500000),2) if random.random() > 0.05 else -1000.0"
            );
            
            Files.write(notebookPath, content.getBytes());
            System.out.println("Dataset.ipynb modified successfully using Java.");

            // Also try modifying target/classes
            Path targetPath = Paths.get("target/classes/static/Dataset.ipynb");
            if (Files.exists(targetPath)) {
                Files.write(targetPath, content.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
