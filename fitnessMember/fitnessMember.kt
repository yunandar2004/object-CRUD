open class FitnessMember(var name: String, val membershipId: String) {
    open fun membershipType(): String = "Basic"
    open fun monthlyFee(): Double = 49.99
    open fun perks(): List<String> = listOf()
}

class PremiumMembers(name: String, membershipId: String) : FitnessMember(name, membershipId) {
    override fun membershipType() = "Premium"
    override fun monthlyFee() = 32.0
    override fun perks() = listOf("Gym Access", "Unlimited Group Classes", "Premium Permissions")
}

class BasicMember(name: String, membershipId: String) : FitnessMember(name, membershipId) {
    override fun membershipType() = "Basic"
    override fun monthlyFee() = 29.99
    override fun perks() = listOf("Gym Access", "Locker")
}

class FamilyMember(name: String, membershipId: String,  val count: Int)
    : FitnessMember(name, membershipId) {
    override fun membershipType() = "Family ($count members)"
    override fun monthlyFee() = 329.99
    override fun perks() = listOf("Full Gym Access", "Kid Classes", "Family Pool")
}

class MemberManager {

    private val familyMembers = mutableListOf<FamilyMember>()
    private val premiumMembers = mutableListOf<PremiumMembers>()
    private val basicMembers = mutableListOf<BasicMember>()

    fun memberExists(id: String): Boolean {
        return (familyMembers + premiumMembers + basicMembers)
            .any { it.membershipId == id }
    }

    fun addMember(member: FitnessMember) {
        when (member) {
            is FamilyMember -> familyMembers.add(member)
            is PremiumMembers -> premiumMembers.add(member)
            is BasicMember -> basicMembers.add(member)
        }
        println("Added: ${member.membershipId} ${member.name} (${member.membershipType()})")
    }

    fun showFamilyMembers() {
        println("\n--- Family Members ---")
        if (familyMembers.isEmpty()) println("None")
        familyMembers.forEach { printMember(it) }
    }

    fun showPremiumMembers() {
        println("\n--- Premium Members ---")
        if (premiumMembers.isEmpty()) println("None")
        premiumMembers.forEach { printMember(it) }
    }

    fun showBasicMembers() {
        println("\n--- Basic Members ---")
        if (basicMembers.isEmpty()) println("None")
        basicMembers.forEach { printMember(it) }
    }

    fun listAllMembers() {
        showFamilyMembers()
        showPremiumMembers()
        showBasicMembers()
    }

    private fun printMember(m: FitnessMember) {
        println("\n${m.name} (${m.membershipId})")
        println("Type: ${m.membershipType()}")
        println("Fee: $${m.monthlyFee()}")
        println("Perks: ${m.perks().joinToString()}")
    }

    fun updateMember(id: String, newName: String? = null, newType: String? = null) {
        val allMembers = familyMembers + premiumMembers + basicMembers
        val member = allMembers.find { it.membershipId == id }

        if (member == null) {
            println(" Member not found.")
            return
        }

        // Remove old member
        familyMembers.removeIf { it.membershipId == id }
        premiumMembers.removeIf { it.membershipId == id }
        basicMembers.removeIf { it.membershipId == id }

        val updatedName = newName ?: member.name
        val updatedType = newType?.lowercase() ?: 
            when (member) {
                is PremiumMembers -> "premium"
                is BasicMember -> "basic"
                is FamilyMember -> "family"
                else -> "basic"
            }

        val updatedMember = when (updatedType) {
            "premium" -> PremiumMembers(updatedName, id)
            "basic"   -> BasicMember(updatedName, id)
            "family"  -> {
                val count = if (member is FamilyMember) member.count else 1
                FamilyMember(updatedName, id, count)
            }
            else -> {
                println(" Invalid type.")
                return
            }
        }

        addMember(updatedMember)
        println("✅ Member updated successfully!")
    }


    fun deleteMember(id: String) {
        val member = (familyMembers + premiumMembers + basicMembers)
            .find { it.membershipId == id }

        if (member == null) {
            println("Member not found.")
            return
        }

        familyMembers.remove(member)
        premiumMembers.remove(member)
        basicMembers.remove(member)

        println("Member deleted successfully.")
    }
}

fun main() {
    val manager = MemberManager()
    var option: String

    do {
        println("\n--- MENU ---")
        println("1. Add Member")
        println("2. Show Basic Members")
        println("3. Show Premium Members")
        println("4. Show Family Members")
        println("5. Show All Members")
        println("6. Update Member")
        println("7. Delete Member")
        println("0. Exit")
        print("Choose: ")

        option = readLine()?.trim().orEmpty()

        when (option) {
            "1" -> {
                print("Enter membership ID: ")
                val id = readLine()?.trim().orEmpty()

                if (manager.memberExists(id)) {
                    println("Membership ID already exists!")
                    continue
                }

                print("Enter name: ")
                val name = readLine()?.trim().orEmpty()

                print("Type (premium/basic/family): ")
                when (readLine()?.trim()?.lowercase()) {
                    "premium" -> manager.addMember(PremiumMembers(name, id))
                    "basic" -> manager.addMember(BasicMember(name, id))
                    "family" -> {
                        print("Enter number of family members: ")
                        val count = readLine()?.toIntOrNull() ?: 1
                        manager.addMember(FamilyMember(name, id, count))
                    }
                    else -> println("Invalid type.")
                }
            }
            "2" -> manager.showBasicMembers()
            "3" -> manager.showPremiumMembers()
            "4" -> manager.showFamilyMembers()
            "5" -> manager.listAllMembers()
            "6" -> {
                    print("Enter membership ID to update: ")
                    val id = readLine()?.trim().orEmpty()

                    print("Enter new name (or press Enter to keep current): ")
                    val newNameInput = readLine()?.trim()
                    val newName = if (newNameInput.isNullOrEmpty()) null else newNameInput

                    print("Enter new membership type (premium/basic/family) OR press Enter to keep current: ")
                    val typeInput = readLine()?.trim()
                    val newType = if (typeInput.isNullOrEmpty()) null else typeInput

                    manager.updateMember(id, newName, newType)
                }

            "7" -> {
                print("Enter ID to delete: ")
                manager.deleteMember(readLine()?.trim().orEmpty())
            }
        }
    } while (option != "0")
    println("Program ended.")
}
