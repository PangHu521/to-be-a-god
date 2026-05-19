package com.deify.app.domain.model

data class Exercise(
    val name: String,
    val sets: Int = 3,
    val reps: Int = 12,
    val weightKg: Float = 0f,
    val durationSeconds: Int = 60
)

data class WorkoutTemplate(
    val category: String,
    val name: String,
    val exercises: List<Exercise>
)

object PresetTemplates {
    val chest = WorkoutTemplate(
        category = "chest",
        name = "胸部训练",
        exercises = listOf(
            Exercise("杠铃卧推", sets = 4, reps = 8),
            Exercise("哑铃上斜卧推", sets = 4, reps = 10),
            Exercise("哑铃飞鸟", sets = 3, reps = 12),
            Exercise("绳索夹胸", sets = 3, reps = 15),
            Exercise("俯卧撑", sets = 3, reps = 20)
        )
    )

    val back = WorkoutTemplate(
        category = "back",
        name = "背部训练",
        exercises = listOf(
            Exercise("引体向上", sets = 4, reps = 8),
            Exercise("杠铃划船", sets = 4, reps = 10),
            Exercise("高位下拉", sets = 3, reps = 12),
            Exercise("坐姿划船", sets = 3, reps = 12),
            Exercise("哑铃单臂划船", sets = 3, reps = 10)
        )
    )

    val legs = WorkoutTemplate(
        category = "legs",
        name = "腿部训练",
        exercises = listOf(
            Exercise("杠铃深蹲", sets = 5, reps = 8),
            Exercise("罗马尼亚硬拉", sets = 4, reps = 10),
            Exercise("腿举", sets = 4, reps = 12),
            Exercise("腿弯举", sets = 3, reps = 12),
            Exercise("小腿提踵", sets = 4, reps = 20)
        )
    )

    val cardio = WorkoutTemplate(
        category = "cardio",
        name = "有氧训练",
        exercises = listOf(
            Exercise("跑步", durationSeconds = 1800),
            Exercise("跳绳", durationSeconds = 600),
            Exercise("波比跳", sets = 5, reps = 15),
            Exercise("开合跳", sets = 4, reps = 30)
        )
    )

    val all = listOf(chest, back, legs, cardio)
}
