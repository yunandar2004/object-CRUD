data class Employee(
    val id: Int,
    val name: String,
    var role:String,
    var baseSalary: Double,
    var annualBonus: Double
)

fun main() {

    val employeeMap = mutableMapOf<Int, Employee>()   

    var input: String

    while(true) {
        println("\nEmployee Management System")
        println("1. Display Employees")
        println("2. Add Employee")
        println("3. Update Employee")
        println("4. Delete Employee")
        println("0. Exit")
        print("Enter choice: ")
        input = readLine()?.trim().orEmpty()

        when (input) {
            // Display employees
            "1" -> {  
                if (employeeMap.isEmpty()) {
                    println("No employees found.")
                } else {
                    println("\n--- Employees Sorted by ID ---")
                    employeeMap.values
                        .sortedBy { it.id }
                        .forEach { e ->
                            println("ID: ${e.id} | Name: ${e.name}| Role:${e.role} | Base Salary: ${e.baseSalary} | Annual Bonus: ${e.annualBonus}")
                        }
                }
            }
            // Add new employee
            "2" -> {
                print("Enter employee ID: ")
                val id = readLine()?.toIntOrNull()?: 0

                if (employeeMap.containsKey(id)) {
                    println("Employee with ID $id already exists.")
                } else {
                    println("Enter name: ")
                    var name = readLine()?.trim()?.uppercase().orEmpty()
                    print("Enter employee role: ")
                    val role = readLine()?.trim()?.lowercase().orEmpty()

                    print("Enter base salary: ")
                    val baseSalary = readLine()?.toDoubleOrNull() ?: 0.0

                    val bonus = when (role) {
                        "developer" -> baseSalary * 0.15
                        "designer"  -> baseSalary * 0.10
                        "manager"   -> baseSalary * 0.20
                        else        -> 0.0
                    }

                    val employee = Employee(id,name, role, baseSalary, bonus)
                    employeeMap[id] = employee  

                    println("Employee added successfully.")
                }
            }

            // Update with ID
            "3" -> {
                print("Enter employee ID to update: ")
                val id = readLine()?.toIntOrNull() ?: 0

                val existingEmployee = employeeMap[id]

                if (existingEmployee != null) {
                    println("Enter name: ")
                    var name = readLine()?.trim()?.uppercase().orEmpty()
                    print("Enter new role: ")
                    val role = readLine()?.trim()?.lowercase().orEmpty()

                    print("Enter new base salary: ")
                    val baseSalary = readLine()?.toDoubleOrNull() ?: 0.0

                    val bonus = when (role) {
                        "developer" -> baseSalary * 0.15
                        "designer"  -> baseSalary * 0.10
                        "manager"   -> baseSalary * 0.20
                        else        -> 0.0
                    }

                    employeeMap[id] = Employee(id, name, role, baseSalary, bonus)
                    println("Employee updated successfully.")
                } else {
                    println("Employee not found.")
                }
            }

            // Delete with ID

            "4" -> {
                print("Enter employee ID to delete: ")
                val id = readLine()?.toIntOrNull() ?: 0

                if (employeeMap.remove(id) != null) {
                    println("Employee deleted successfully.")
                } else {
                    println("Employee not found.")
                }
            }

            "0" -> println("Exiting...")

            else -> println("Invalid choice. Please try again.")
        }
        println("Do you want to use this program again (yes/no): ")
        var choice = readLine()?.trim()?.lowercase().orEmpty()
        if(choice != "yes"){
            break
        }

    }  

    println("Program ended.")
}
