package halla.icsw.acca_kotlin.Maintenance

class Cycle(cycle: Double) {
    var cycle = cycle

    fun getPartData(distance: Double): PartData {
        var remainingDistance = cycle - distance // 부품교체까지 남은 거리
        var statusPart = 0 // 부품교체까지 남은 거리에 대한 상태

        statusPart = if (distance / cycle * 100 > 66)
            0 // 경고
        else if (distance / cycle * 100 <= 66 && distance / cycle * 100 > 33)
            1 // 주의
        else
            2 // 양호

        return PartData(remainingDistance, statusPart)
    }
}