import java.awt.Color
import java.awt.Graphics2D
import java.awt.Polygon
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

abstract class Entity (
    open var x: Int = 100,
    open var y: Int = 100) {

    fun adjustCoordinates(xAdj: Int, yAdj: Int) {
        x += xAdj
        y += yAdj
    }

    abstract fun applyImage(graphics: Graphics2D)
    abstract fun containsPoint(clickX: Int, clickY: Int): Boolean


}


data class PolygonEntity (
    override var x: Int = 100,
    override var y: Int = 100,
    val polygon: Polygon,
    val polygonColor: Color): Entity(x,y) {

    override fun applyImage(graphics: Graphics2D) {
        graphics.color = polygonColor
        graphics.fillPolygon(getPolyAtAdjustedCoords())
    }

    fun getPolyAtAdjustedCoords(): Polygon {
        val newPoly = Polygon()
        for (i in 0 until polygon.npoints) {
            newPoly.addPoint(polygon.xpoints[i] + x, polygon.ypoints[i] + y)
        }
        return newPoly
    }

    override fun containsPoint(clickX: Int, clickY: Int): Boolean {
        return getPolyAtAdjustedCoords().contains(clickX, clickY)
    }


}


 data class ImageEntity(
     override var x: Int = 100,
     override var y: Int = 100,
     val imageName: String): Entity(x,y) {

    val image: BufferedImage = ImageIO.read(this.javaClass.getResourceAsStream(imageName))

    override fun applyImage(graphics: Graphics2D) {
        graphics.drawImage(image, x, y , null)
    }

     override fun containsPoint(clickX: Int, clickY: Int): Boolean {
         return (clickX >= x) && (clickX <= x + image.width) && (clickY >= y) && (clickY <= y + image.height)
     }

}