package com.example.attendance_app_2.db

object BranchYearMapper {
    val codeToBranch = hashMapOf<Int, String>()
    val branchTOCode = hashMapOf<String, Int>()
    init {
        codeToBranch.put(1, "CSE")
        codeToBranch.put(2, "CIV")
        codeToBranch.put(3, "CST")
        codeToBranch.put(4, "ECE")
        codeToBranch.put(5, "MEC")
        codeToBranch.put(6, "CSB")
        codeToBranch.put(7, "EEE")
        codeToBranch.put(8, "CSD")
        codeToBranch.put(9, "CSM")

        branchTOCode.put("CSE", 1)
        branchTOCode.put("CIV", 2)
        branchTOCode.put("CST", 3)
        branchTOCode.put("ECE", 4)
        branchTOCode.put("MEC", 5)
        branchTOCode.put("CSB", 6)
        branchTOCode.put("EEE", 7)
        branchTOCode.put("CSD", 8)
        branchTOCode.put("CSM", 9)
    }

    fun getBranchName(code: Int): String = codeToBranch[code]!!

    fun getBranchCode(name: String): Int = branchTOCode[name]!!
}