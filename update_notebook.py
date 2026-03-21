import json

notebook_path = r"c:\Users\janan\Downloads\RegReportX\src\main\resources\static\Dataset.ipynb"
with open(notebook_path, 'r', encoding='utf-8') as f:
    nb = json.load(f)

for cell in nb.get('cells', []):
    if cell['cell_type'] == 'code':
        source = "".join(cell['source'])
        
        # Modify LoanAmount (make 5% negative)
        source = source.replace(
            '"LoanAmount": round(random.uniform(50000,10000000),2),',
            '"LoanAmount": round(random.uniform(50000,10000000),2) if random.random() > 0.05 else round(random.uniform(-10000, -100),2),'
        )
        
        # Modify Loan InterestRate (make 5% over 20)
        source = source.replace(
            '"InterestRate": round(random.uniform(6.5,14),2),',
            '"InterestRate": round(random.uniform(6.5,14),2) if random.random() > 0.05 else round(random.uniform(21, 30),2),'
        )

        # Modify Deposit Amount (make 5% negative)
        source = source.replace(
            '"Amount": round(random.uniform(1000,5000000),2),',
            '"Amount": round(random.uniform(1000,5000000),2) if random.random() > 0.05 else round(random.uniform(-5000, -100),2),'
        )

        # Modify GL Debit (make 5% negative)
        source = source.replace(
            'debit = round(random.uniform(1000,500000),2)',
            'debit = round(random.uniform(1000,500000),2) if random.random() > 0.05 else -1000.0'
        )

        # Modify GL Credit (make 5% negative)
        source = source.replace(
            'credit = round(random.uniform(1000,500000),2)',
            'credit = round(random.uniform(1000,500000),2) if random.random() > 0.05 else -1000.0'
        )
        
        lines = source.splitlines(True)
        cell['source'] = lines

with open(notebook_path, 'w', encoding='utf-8') as f:
    json.dump(nb, f, indent=2)

# Also update the copy in target/classes if it exists, to ensure it doesn't run the clean one if they don't rebuild
try:
    notebook_path_target = r"c:\Users\janan\Downloads\RegReportX\target\classes\static\Dataset.ipynb"
    with open(notebook_path_target, 'w', encoding='utf-8') as f:
        json.dump(nb, f, indent=2)
except Exception:
    pass

print("Notebook updated successfully with 5% error rates.")
