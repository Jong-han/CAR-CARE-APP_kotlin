package halla.icsw.acca_kotlin.Maintenance

class Cycle(cycle: Double) {
    var cycle = cycle

    fun getPartData(distance: Double): PartData {
        var remainingDistance = cycle - distance // 부품교체까지 남은 거리

        var statusPart: Int = when {
            remainingDistance < 1000 -> 0
            (distance / cycle * 100) > 50 && remainingDistance >= 1000 -> 1
            else -> 2
        }

        return PartData(remainingDistance, statusPart)
    }
}