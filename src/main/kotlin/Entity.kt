import java.awt.Polygon
import java.awt.image.BufferedImage

data class Entity (var x: Int = 100, var y: Int = 100, val polygon: Polygon, val image: BufferedImage? = null) {

    fun adjustCoordinates(xAdj: Int, yAdj: Int) {
        x += xAdj
        y += yAdj
    }

    fun getPolyAtAdjustedCoords(): Polygon {
        val newPoly = Polygon()
        for (i in 0 until polygon.npoints) {
            newPoly.addPoint(polygon.xpoints[i] + x, polygon.ypoints[i] + y)
        }
        return newPoly
    }

    fun containsPoint(clickX: Int, clickY: Int): Boolean {
        return getPolyAtAdjustedCoords().contains(clickX, clickY)
    }

}