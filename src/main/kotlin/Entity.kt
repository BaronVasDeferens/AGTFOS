import java.awt.Polygon
import java.awt.image.BufferedImage

data class Entity (var x: Int = 100, var y: Int = 100, val polygon: Polygon, val image: BufferedImage? = null) {

    init {

    }

    fun adjustCoordinates(xAdj: Int, yAdj: Int) {
        x += xAdj
        y += yAdj
    }

    fun getPolyAtCoords(): Polygon {
        val newPoly = Polygon()
        for (i in 0 until polygon.npoints) {
            newPoly.addPoint(polygon.xpoints[i] + x, polygon.ypoints[i] + y)
        }
        return newPoly
    }

    fun containsPoint(clickX: Int, clickY: Int): Boolean {
        return getPolyAtCoords().contains(clickX, clickY)
    }

}