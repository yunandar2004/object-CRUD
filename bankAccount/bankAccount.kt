data class Transaction(
    val type: String,
    val amount: Double,
    val balanceAfter: Double,
    val note: String = ""
)
class Account(
     val accountNumber: Int,
    val ownerName: String,
    initialBalance: Double
) {

    private var _balance = initialBalance
    val balance: Double
        get() = _balance

    private val _history = mutableListOf<Transaction>()
    val history: List<Transaction>
        get() = _history
    
    fun addTransaction(type: String, amount: Double, note: String = "") {
        _history.add(Transaction(type, amount, _balance, note))
    }

    fun deposit(amount: Double) {
        _balance += amount
        addTransaction("DEPOSIT", amount)
    }

    fun withdraw(amount: Double, receiver: String = "Unknown"): Boolean {
        if (amount > _balance) return false
        _balance -= amount
        addTransaction("WITHDRAW", amount, "Receiver: $receiver")
        return true
    }



}
class Bank {

    private val accounts = mutableMapOf<Int, Account>()
    private var nextAccountNumber = 1001

    fun createAccount(owner: String, initialDeposit: Double): Int {
        val acc = Account(nextAccountNumber, owner, initialDeposit)
        acc.addTransaction("CREATE", initialDeposit, "Initial Deposit")

        accounts[nextAccountNumber] = acc
        return nextAccountNumber++
    }

    fun deposit(accNo: Int, amount: Double): Boolean {
        val acc = accounts[accNo] ?: return false
        acc.deposit(amount)
        return true
    }

    fun withdraw(accNo: Int, amount: Double, receiver: String): Boolean {
        val acc = accounts[accNo] ?: return false
        return acc.withdraw(amount, receiver)
    }


    fun transfer(fromAcc: Int, toAcc: Int, amount: Double): Boolean {
        val sender = accounts[fromAcc] ?: return false
        val receiver = accounts[toAcc] ?: return false

        if (!sender.withdraw(amount)) return false
        receiver.deposit(amount)

        sender.addTransaction("TRANSFER OUT", amount, "To $toAcc")
        receiver.addTransaction("TRANSFER IN", amount, "From $fromAcc")

        return true
    }

    fun showAccount(accNo: Int) {
        val acc = accounts[accNo] ?: run {
            println("Account not found.")
            return
        }

        println("\n--- Account Info ---")
        println("Account Number: ${acc.accountNumber}")
        println("Owner: ${acc.ownerName}")
        println("Balance: ${acc.balance}")
    }

    fun showAllAccounts() {
        if (accounts.isEmpty()) {
            println("No accounts exist.")
            return
        }

        println("\n--- All Accounts ---")
        accounts.values.sortedBy { it.accountNumber }.forEach {
            println("AccNo: ${it.accountNumber} | Name: ${it.ownerName} | Balance: ${it.balance}")
        }
    }

    fun showHistory(accNo: Int) {
        val acc = accounts[accNo] ?: run {
            println("Account not found.")
            return
        }

        println("\n--- Transaction History for Account $accNo ---")
        if (acc.history.isEmpty()) {
            println("No transactions yet.")
            return
        }

        acc.history.forEach {
            println("${it.type} | Amount: ${it.amount} | Balance: ${it.balanceAfter} | ${it.note}")
        }
    }

    fun deleteAccount(accNo: Int): Boolean {
        return accounts.remove(accNo) != null
    }
}
fun main() {

    val bank = Bank()
    var choice: Int

    do {
        println("\n=========== BANK SYSTEM ===========")
        println("1. Create Account")
        println("2. Deposit")
        println("3. Withdraw")
        println("4. Transfer")
        println("5. Show Account")
        println("6. Show All Accounts")
        println("7. Show Transaction History")
        println("8. Delete Account")
        println("0. Exit")
        print("Choose: ")

        choice = readLine()?.trim()?.toIntOrNull() ?: 0

        when (choice) {

            1 -> {
                print("Enter owner name: ")
                val name = readLine()?.trim().orEmpty()

                print("Enter initial deposit: ")
                val deposit = readLine()?.trim()?.toDoubleOrNull() ?: 0.0

                val accNo = bank.createAccount(name, deposit)
                println("Account created! Account Number = $accNo")
            }

            2 -> {
                print("Account Number: ")
                val accNo = readLine()?.toIntOrNull() ?: -1

                print("Amount: ")
                val amount = readLine()?.toDoubleOrNull() ?: 0.0

                if (bank.deposit(accNo, amount)) println("Deposit successful!")
                else println("Deposit failed.")
            }

            3 -> {
                print("Account Number: ")
                val accNo = readLine()?.toIntOrNull() ?: -1

                print("Amount: ")
                val amount = readLine()?.toDoubleOrNull() ?: 0.0

                print("Receiver : ")
                val receiver = readLine()?.trim().orEmpty()

                if (bank.withdraw(accNo, amount, receiver))
                    println("Withdrawal successful!")
                else
                    println("Withdrawal failed.")

            }

            4 -> {
                print("From Account: ")
                val from = readLine()?.toIntOrNull() ?: -1

                print("To Account: ")
                val to = readLine()?.toIntOrNull() ?: -1

                print("Amount: ")
                val amount = readLine()?.toDoubleOrNull() ?: 0.0

                if (bank.transfer(from, to, amount)) println("Transfer successful!")
                else println("Transfer failed.")
            }

            5 -> {
                print("Enter account number: ")
                val accNo = readLine()?.toIntOrNull() ?: -1
                bank.showAccount(accNo)
            }

            6 -> bank.showAllAccounts()

            7 -> {
                print("Enter account number: ")
                val accNo = readLine()?.toIntOrNull() ?: -1
                bank.showHistory(accNo)
            }

            8 -> {
                print("Enter account number: ")
                val accNo = readLine()?.toIntOrNull() ?: -1

                if (bank.deleteAccount(accNo)) println("Account deleted.")
                else println("Account not found.")
            }
        }

    } while (choice != 0)

    println("Program Ended.")
}
