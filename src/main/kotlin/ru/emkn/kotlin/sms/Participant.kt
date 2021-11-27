package ru.emkn.kotlin.sms

class Participant {
    val wishGroup: String
    val sex: Sex
    val surname: String
    val name: String
    val yearOfBirth: Int
    val rank: String
    var collective: String = ""
        private set
    var points: Int = 0
        private set

    constructor(
        group: String,
        sex: Sex,
        surname: String,
        name: String,
        yearOfBirth: Int,
        rank: String
    ) {
        this.wishGroup = group
        this.sex = sex
        this.surname = surname
        this.name = name
        this.yearOfBirth = yearOfBirth
        this.rank = rank
    }

    var number: Int = -1
    var startTime: Time = Time(0)

    fun setStart(num: Int, start:Time)
    {
        number=num
        startTime=start
    }

    fun setPoints(points: Int) {
        this.points = points
    }

    fun setCollective(nameOfCollective: String) {
        if (this.collective == "") { //поменять коллектив можно только один раз
            this.collective = nameOfCollective
        }
    }

    override fun toString(): String {
        return "Группа: $wishGroup, Пол: $sex, Фамилия: $surname, Имя: $name, Год рождения: $yearOfBirth, Разряд: $rank "
    }

}