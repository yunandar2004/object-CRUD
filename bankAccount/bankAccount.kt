data class Transaction(
    val type: String,
    val amount: Double,
    val balanceAfter: Double,
    val note: String = ""
)

class Account(
    val accountNumber: Int,
    var ownerName: String,  // Changed to var to allow updates
    initialBalance: Double
) {

    private var _balance = initialBalance
    val balance: Double
        get() = _balance

    private val _history = mutableListOf<Transaction>()
    val history: List<Transaction>
        get() = _history
    
    fun addTransaction(type: String, amount: Double, balanceAfter: Double, note: String = "") {
        _history.add(Transaction(type, amount, balanceAfter, note))
    }

    fun deposit(amount: Double, note: String = "") {
        _balance += amount
        addTransaction("DEPOSIT", amount, _balance, note)
    }

    fun withdraw(amount: Double, note: String = ""): Boolean {
        if (amount > _balance) return false
        _balance -= amount
        addTransaction("WITHDRAW", amount, _balance, note)
        return true
    }
    
    // New method to update account details
    fun updateAccount(newName: String? = null): Boolean {
        var updated = false
        
        if (newName != null && newName.isNotBlank() && newName != ownerName) {
            val oldName = ownerName
            ownerName = newName
            addTransaction("ACCOUNT UPDATE", 0.0, _balance, "Name changed from '$oldName' to '$newName'")
            updated = true
            println("Account owner name updated: $oldName to $newName")
        }
        
        return updated
    }
}

class Bank {

    private val accounts = mutableMapOf<Int, Account>()
    private var nextAccountNumber = 1001

    fun createAccount(owner: String, initialDeposit: Double): Int {
        val acc = Account(nextAccountNumber, owner, initialDeposit)
        acc.addTransaction("CREATE", initialDeposit, initialDeposit, "Initial Deposit")
        accounts[nextAccountNumber] = acc
        return nextAccountNumber++
    }

    fun deposit(accNo: Int, amount: Double, note: String): Boolean {
        val acc = accounts[accNo] ?: return false
        acc.deposit(amount, note)
        return true
    }

    fun withdraw(accNo: Int, amount: Double, note: String): Boolean {
        val acc = accounts[accNo] ?: return false
        return acc.withdraw(amount, note)
    }

    fun transfer(fromAcc: Int, toAcc: Int, amount: Double): Boolean {
        // Check if both accounts exist
        if (fromAcc == toAcc) {
            println("Cannot transfer to the same account!")
            return false
        }
        
        val sender = accounts[fromAcc]
        val receiver = accounts[toAcc]
        
        // Show current status before transfer
        println("\n--- Before Transfer ---")
        println("Sender Account $fromAcc: ${sender?.let { "Balance: $${it.balance}" } ?: "Account not found"}")
        println("Receiver Account $toAcc: ${receiver?.let { "Balance: $${it.balance}" } ?: "Account not found"}")
        println("Transfer Amount: $$amount")
        
        if (sender == null) {
            println("Error: Sender account $fromAcc does not exist!")
            return false
        }
        
        if (receiver == null) {
            println("Error: Receiver account $toAcc does not exist!")
            return false
        }
        
        // Check if sender balance
        if (amount > sender.balance) {
            println("Error: Insufficient funds! Sender balance: $${sender.balance}, Transfer amount: $$amount")
            return false
        }
        
        // Check for invalid amount
        if (amount <= 0) {
            println("Error: Transfer amount must be positive!")
            return false
        }
        
        // Perform the transfer
        if (!sender.withdraw(amount, "Transfer to account $toAcc")) {
            return false
        }
        receiver.deposit(amount, "Transfer from account $fromAcc")

        // Show status after transfer
        println("\n--- After Transfer ---")
        println("Sender Account $fromAcc: Balance: $${sender.balance}")
        println("Receiver Account $toAcc: Balance: $${receiver.balance}")
        println("Transfer successful! $$amount transferred from account $fromAcc to $toAcc")
        
        return true
    }
    
    // NEW: Update account information
    fun updateAccount(accNo: Int, newName: String ): Boolean {


        val acc = accounts[accNo]
        if (acc == null) {
            println("Account $accNo not found!")
            return false
        }
        
        println("\n--- Updating Account $accNo ---")
        println("Current owner: ${acc.ownerName}")
        println("Current balance: $${acc.balance}")
        return acc.updateAccount(newName)
    }
    
    // NEW: View detailed account information
    fun viewAccountDetails(accNo: Int) {
        val acc = accounts[accNo] ?: run {
            println("Account $accNo not found!")
            return
        }
        
        println("\n=== Account Details ===")
        println("Account Number: ${acc.accountNumber}")
        println("Owner Name: ${acc.ownerName}")
        println("Current Balance: $${"%.2f".format(acc.balance)}")
        println("Number of Transactions: ${acc.history.size}")
        println("Account Created: ${acc.history.firstOrNull { it.type == "CREATE" }?.let { 
            "On initial deposit of $${it.amount}" } ?: "Date not available"}")
        println("Last Transaction: ${acc.history.lastOrNull()?.let { 
            "${it.type} of $${it.amount} on ${it.note}" } ?: "No transactions yet"}")
    }


    fun showAccount(accNo: Int) {
        val acc = accounts[accNo] ?: run {
            println("Account not found.")
            return
        }

        println("\n--- Account Info ---")
        println("Account Number: ${acc.accountNumber}")
        println("Owner: ${acc.ownerName}")
        println("Balance: $${acc.balance}")
    }
    
    fun getAccount(accNo: Int): Account? {
        return accounts[accNo]
    }

    fun showAllAccounts() {
        if (accounts.isEmpty()) {
            println("No accounts exist.")
            return
        }

        println("\n--- All Accounts ---")
        println("Total Accounts: ${accounts.size}")
        println("=".repeat(50))
        accounts.values.sortedBy { it.accountNumber }.forEach {
            println("AccNo: ${it.accountNumber} | Name: ${it.ownerName} | Balance: $${"%.2f".format(it.balance)}")
        }
        println("=".repeat(50))
    }

    fun showHistory(accNo: Int) {
        val acc = accounts[accNo] ?: run {
            println("Account not found.")
            return
        }

        println("\n--- Transaction History for Account $accNo (${acc.ownerName}) ---")
        if (acc.history.isEmpty()) {
            println("No transactions yet.")
            return
        }

        acc.history.forEachIndexed { index, transaction ->
            println("${index + 1}. ${transaction.type.padEnd(15)} | Amount: $${"%.2f".format(transaction.amount)} | " +
                   "Balance: $${"%.2f".format(transaction.balanceAfter)} | ${transaction.note}")
        }
        println("Total Transactions: ${acc.history.size}")
    }

    fun deleteAccount(accNo: Int): Boolean {
        val acc = accounts[accNo] ?: return false
        
        // Check if account has balance before deletion
        if (acc.balance > 0) {
            println("Warning: Account has $${acc.balance} balance! Withdraw all funds before deletion.")
            print("Do you want to force delete? (yes/no): ")
            val response = readLine()?.trim()?.lowercase()
            if (response != "yes") {
                println("Deletion cancelled.")
                return false
            }
        }
        
        accounts.remove(accNo)
        println("Account $accNo (${acc.ownerName}) deleted successfully.")
        return true
    }
    
    fun accountExists(accNo: Int): Boolean {
        return accounts.containsKey(accNo)
    }

}

fun main() {
    val bank = Bank()
    var choice: Int

    do {
        println("\n" + "=".repeat(40))
        println("          BANK MANAGEMENT SYSTEM")
        println("=".repeat(40))
        println("1. Create New Account")
        println("2. Deposit Money")
        println("3. Withdraw Money")
        println("4. Transfer Between Accounts")
        println("5. View Account Details")
        println("6. Update Account Information")
        println("7. Show All Accounts")
        println("8. Show Transaction History")
        println("9. Delete Account")
        println("10. Check Account Exists")
        println("0. Exit")
        print("Choose option: ")

        choice = readLine()?.trim()?.toIntOrNull() ?: 0

        when (choice) {
            1 -> {
                print("Enter owner name: ")
                val name = readLine()?.trim().orEmpty()

                if (name.isEmpty()) {
                    println("Owner name cannot be empty!")
                    continue
                }

                print("Enter initial deposit: ")
                val deposit = readLine()?.trim()?.toDoubleOrNull() ?: 0.0

                if (deposit < 0) {
                    println("Initial deposit cannot be negative!")
                } else if (deposit == 0.0) {
                    print("Create account with zero balance? (yes/no): ")
                    val confirm = readLine()?.trim()?.lowercase()
                    if (confirm == "yes") {
                        val accNo = bank.createAccount(name, deposit)
                        println("Account created! Account Number = $accNo")
                    } else {
                        println("Account creation cancelled.")
                    }
                } else {
                    val accNo = bank.createAccount(name, deposit)
                    println("Account created! Account Number = $accNo")
                }
            }

            2 -> {
                print("Account Number: ")
                val accNo = readLine()?.toIntOrNull() ?: -1

                print("Amount: ")
                val amount = readLine()?.toDoubleOrNull() ?: 0.0
                
                if (amount <= 0) {
                    println("Deposit amount must be positive!")
                } else {
                    print("Note: ")
                    val note = readLine()?.trim().orEmpty()

                    if (bank.deposit(accNo, amount, note)) {
                        println("Deposit successful!")
                        bank.showAccount(accNo)  // Show updated balance
                    } else {
                        println("Deposit failed. Account not found.")
                    }
                }
            }

            3 -> {
                print("Account Number: ")
                val accNo = readLine()?.toIntOrNull() ?: -1

                print("Amount: ")
                val amount = readLine()?.toDoubleOrNull() ?: 0.0
                
                if (amount <= 0) {
                    println("Withdrawal amount must be positive!")
                } else {
                    print("Note : ")
                    val note = readLine()?.trim().orEmpty()

                    if (bank.withdraw(accNo, amount, note)) {
                        println("Withdraw successful!")
                        bank.showAccount(accNo)  // Show updated balance
                    } else {
                        println("Withdraw failed. Check account number and balance.")
                    }
                }
            }

            4 -> {
                print("From Account: ")
                val from = readLine()?.toIntOrNull() ?: -1

                print("To Account: ")
                val to = readLine()?.toIntOrNull() ?: -1

                print("Amount: ")
                val amount = readLine()?.toDoubleOrNull() ?: 0.0

                if (bank.transfer(from, to, amount)) {
                    println("\nTransfer completed successfully!")
                } else {
                    println("\nTransfer failed.")
                }
            }

            5 -> {
                print("Enter account number: ")
                val accNo = readLine()?.toIntOrNull() ?: -1
                bank.viewAccountDetails(accNo)
            }

            6 -> {
                print("Enter account number to update: ")
                val accNo = readLine()?.toIntOrNull() ?: -1
                
                print("Enter new owner name : ")
                val owner = readLine()?.trim().orEmpty()
                
                if (owner?.isEmpty() == true) {
                    println("No changes made.")
                } else {
                    if (bank.updateAccount(accNo, owner)) {
                        println("Account updated successfully!")
                    } else {
                        println("Update failed.")
                    }
                }
            }

            7 -> bank.showAllAccounts()

            8 -> {
                print("Enter account number: ")
                val accNo = readLine()?.toIntOrNull() ?: -1
                bank.showHistory(accNo)
            }

            9 -> {
                print("Enter account number: ")
                val accNo = readLine()?.toIntOrNull() ?: -1

                if (bank.deleteAccount(accNo)) {
                    println("Account deleted successfully.")
                } else {
                    println("Account deletion failed or cancelled.")
                }
            }

            10 -> {
                print("Enter account number: ")
                val accNo = readLine()?.toIntOrNull() ?: -1
                
                if (bank.accountExists(accNo)) {
                    println("Account $accNo exists.")
                    val account = bank.getAccount(accNo)
                    println("Owner: ${account?.ownerName}, Balance: $${account?.balance}")
                } else {
                    println("Account $accNo does not exist.")
                }
            }

        }

    } while (choice != 0)

    println("\nThank you for using Bank Management System!")
    println("Program Ended.")
}