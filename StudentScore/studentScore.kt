data class Students(var id: String,val name: String, val score: Int)

fun main (){
    var studentList = mutableListOf<Students>()
    do{
        println("What do you want to do?")
        println("1. Add student")
        println("2. View student")
        println("3. Remove student")
        println("4. Update student")
        println("0. Exit")
        var choice = readLine()?.trim()?.toIntOrNull()?:0
        when(choice){

            1 -> {
                println("==Add student==")
                println("Enter student ID :")
                var id = readLine()?.trim().orEmpty()
                println("Enter student name:")
                var name = readLine()?.trim().orEmpty()
                println("Enter student score:")
                var score = readLine()?.trim()?.toIntOrNull()?:0
                studentList.add(Students(id,name,score))
            }
            2 -> {
                println("==View student==")
                if(studentList.isEmpty()){
                    println("No student found")
                }
                else{
                    println("==Student List==")
                     for (student in studentList){
                    println("Name: ${student.name}, Score: ${student.score}")
                }
                }
                   
                // println("Enter student name:")
                // var name = readLine()?.trim().orEmpty()
                // studentList.remove(Students(name,0))
            }
            3 -> {
                println("Enter student Id to remove")
                // var inputId = readLine()?.trim()?.toIntOrNull()?:-1
                var inputId = readLine()?.trim().orEmpty()
                var result = studentList.removeIf {it.id== inputId }
                if(result){
                    println("Student removed successfully")
                }
                else{
                    println("Student not found")
                }
                println("==Student List==")
                for (student in studentList){
                    println("Name: ${student.name}, Score: ${student.score}")
                }
            }
            4 ->{
                println("Enter student Id to update")
                var inputId = readLine()?.trim().orEmpty()
                var target = studentList.find{ it.id == inputId }
                if(target != null){
                    var result = studentList.indexOf(target)
                    println("Enter student name:")
                    var name = readLine()?.trim().orEmpty()
                    println("Enter student score:")
                    var score = readLine()?.trim()?.toIntOrNull()?:0
                    studentList[result] = Students(inputId,name,score)
                    println("Student updated successfully")
                }
                else{
                    println("Student not found")
                }
                println("==Student List==")
                for (student in studentList){
                    println("Name: ${student.name}, Score: ${student.score}")
                }

            }
            0 -> {
                break
            }
            else -> {
                println("Invalid choice")
            }
        }
    }while(true)
    

}

