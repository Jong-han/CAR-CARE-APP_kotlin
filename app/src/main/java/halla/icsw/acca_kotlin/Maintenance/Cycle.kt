package halla.icsw.acca_kotlin.Maintenance

class Cycle(cycle: Double) {
    var cycle = cycle

    fun getPartData(distance: Double): PartData {
        var remainingDistance = cycle - distance // 부품교체까지 남은 거리
        var status = 0 // 부품교체까지 남은 거리에 대한 상태

        if (distance / 40000 * 100 > 66)
            status = 0 // 경고
        else if (distance / 40000 * 100 <= 66 && distance / 40000 * 100 > 33)
            status = 1 // 주의
        else
            status = 2 // 양호

        return PartData(remainingDistance, status)
    }
}