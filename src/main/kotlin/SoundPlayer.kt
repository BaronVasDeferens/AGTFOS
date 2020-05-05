import javax.sound.sampled.AudioSystem

class SoundPlayer(val filePath: String) {

    val clip = AudioSystem.getClip()
    val audioInputStream = AudioSystem.getAudioInputStream(javaClass.getResourceAsStream(filePath))


    init {
        clip.open(audioInputStream)
    }

    fun play() {
        clip.stop()
        clip.framePosition = 0
        clip.start()
    }


}