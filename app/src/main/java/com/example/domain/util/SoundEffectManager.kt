package com.example.domain.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

data class ToneSpec(
    val frequencyHz: Double,
    val durationMs: Int,
    val amplitude: Double = 0.35,
    val startDelayMs: Int = 0
)

/**
 * Zero-permission synthesized PCM audio engine for crisp fintech UI feedback.
 * All audio synthesis runs asynchronously on Dispatchers.Default and safely no-ops
 * in headless JVM test environments where Android AudioTrack is unavailable.
 */
object SoundEffectManager {

    private const val SAMPLE_RATE = 44100
    private val soundScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Volatile
    var isEnabled: Boolean = true

    /**
     * Gentle ascending fintech triad (C6 -> E6 -> G6) for saved transactions,
     * transfers, vault deposits, bill payments, and settled debts.
     */
    fun playSuccessChime() {
        if (!isEnabled) return
        playTonesAsync(
            listOf(
                ToneSpec(frequencyHz = 1046.50, durationMs = 65, amplitude = 0.28, startDelayMs = 0),
                ToneSpec(frequencyHz = 1318.51, durationMs = 65, amplitude = 0.30, startDelayMs = 55),
                ToneSpec(frequencyHz = 1567.98, durationMs = 140, amplitude = 0.34, startDelayMs = 110)
            )
        )
    }

    /**
     * Distinct two-pulse bell chime (F5 -> A5 -> C6) for budget threshold warnings,
     * unusual expense detection, and foreground notification alerts.
     */
    fun playAlertChime() {
        if (!isEnabled) return
        playTonesAsync(
            listOf(
                ToneSpec(frequencyHz = 698.46, durationMs = 95, amplitude = 0.36, startDelayMs = 0),
                ToneSpec(frequencyHz = 880.00, durationMs = 110, amplitude = 0.38, startDelayMs = 105),
                ToneSpec(frequencyHz = 1046.50, durationMs = 170, amplitude = 0.40, startDelayMs = 220)
            )
        )
    }

    /**
     * Smooth harmonic unlock chord (G5 -> B5 -> D6) for Master PIN & Biometric unlock.
     */
    fun playUnlockChime() {
        if (!isEnabled) return
        playTonesAsync(
            listOf(
                ToneSpec(frequencyHz = 783.99, durationMs = 60, amplitude = 0.26, startDelayMs = 0),
                ToneSpec(frequencyHz = 987.77, durationMs = 60, amplitude = 0.28, startDelayMs = 50),
                ToneSpec(frequencyHz = 1174.66, durationMs = 135, amplitude = 0.32, startDelayMs = 100)
            )
        )
    }

    /**
     * Muted low double warning tone (A3 -> F3) for wrong PIN or invalid inputs.
     */
    fun playErrorTone() {
        if (!isEnabled) return
        playTonesAsync(
            listOf(
                ToneSpec(frequencyHz = 246.94, durationMs = 85, amplitude = 0.34, startDelayMs = 0),
                ToneSpec(frequencyHz = 185.00, durationMs = 135, amplitude = 0.36, startDelayMs = 95)
            )
        )
    }

    /**
     * Brief descending soft tick (E5 -> B4) for deletions & data wipe actions.
     */
    fun playDeleteSound() {
        if (!isEnabled) return
        playTonesAsync(
            listOf(
                ToneSpec(frequencyHz = 659.25, durationMs = 45, amplitude = 0.24, startDelayMs = 0),
                ToneSpec(frequencyHz = 493.88, durationMs = 75, amplitude = 0.22, startDelayMs = 40)
            )
        )
    }

    /**
     * Celebratory 4-note sparkle (C6 -> E6 -> G6 -> C7) for Achievement Locker & streak milestones.
     */
    fun playAchievementChime() {
        if (!isEnabled) return
        playTonesAsync(
            listOf(
                ToneSpec(frequencyHz = 1046.50, durationMs = 65, amplitude = 0.26, startDelayMs = 0),
                ToneSpec(frequencyHz = 1318.51, durationMs = 65, amplitude = 0.28, startDelayMs = 60),
                ToneSpec(frequencyHz = 1567.98, durationMs = 75, amplitude = 0.30, startDelayMs = 120),
                ToneSpec(frequencyHz = 2093.00, durationMs = 180, amplitude = 0.34, startDelayMs = 185)
            )
        )
    }

    /**
     * Ultra-brief 14ms soft tactile tap for numeric keypad & PIN pad keys.
     */
    fun playSoftKeyTick() {
        if (!isEnabled) return
        playTonesAsync(
            listOf(
                ToneSpec(frequencyHz = 960.0, durationMs = 14, amplitude = 0.14, startDelayMs = 0)
            )
        )
    }

    internal fun generateToneSequencePcm(
        tones: List<ToneSpec>,
        sampleRate: Int = SAMPLE_RATE
    ): ShortArray {
        if (tones.isEmpty()) return ShortArray(0)
        val totalDurationMs = tones.maxOf { it.startDelayMs + it.durationMs }
        val totalSamples = ((totalDurationMs / 1000.0) * sampleRate).toInt().coerceAtLeast(1)
        val mixBuffer = DoubleArray(totalSamples)

        for (tone in tones) {
            val startSample = ((tone.startDelayMs / 1000.0) * sampleRate).toInt()
            val toneSamples = ((tone.durationMs / 1000.0) * sampleRate).toInt().coerceAtLeast(1)
            val attackSamples = (0.008 * sampleRate).toInt().coerceIn(1, toneSamples / 3 + 1)

            for (i in 0 until toneSamples) {
                val idx = startSample + i
                if (idx >= totalSamples) break

                val progress = i.toDouble() / toneSamples.toDouble()
                val attack = if (i < attackSamples) i.toDouble() / attackSamples.toDouble() else 1.0
                // Smooth exponential-cosine decay for click-free warm bell tail
                val decay = kotlin.math.exp(-3.2 * progress) * (1.0 - progress * progress * 0.25)
                val env = attack * decay

                val t = i.toDouble() / sampleRate.toDouble()
                val fundamental = sin(2.0 * PI * tone.frequencyHz * t)
                val warmHarmonic = 0.22 * sin(2.0 * PI * (tone.frequencyHz * 2.0) * t)
                val sampleVal = (fundamental + warmHarmonic) * 0.80 * tone.amplitude * env

                mixBuffer[idx] += sampleVal
            }
        }

        return ShortArray(totalSamples) { idx ->
            val clamped = mixBuffer[idx].coerceIn(-0.95, 0.95)
            (clamped * Short.MAX_VALUE).toInt().toShort()
        }
    }

    private fun playTonesAsync(tones: List<ToneSpec>) {
        soundScope.launch {
            runCatching {
                val pcm = generateToneSequencePcm(tones)
                if (pcm.isEmpty()) return@runCatching

                val byteCount = pcm.size * 2
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                val audioFormat = AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()

                val track = AudioTrack.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setAudioFormat(audioFormat)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .setBufferSizeInBytes(byteCount)
                    .setSessionId(AudioManager.AUDIO_SESSION_ID_GENERATE)
                    .build()

                try {
                    track.write(pcm, 0, pcm.size)
                    track.play()
                    val sleepMs = tones.maxOf { it.startDelayMs + it.durationMs } + 60L
                    Thread.sleep(sleepMs)
                } finally {
                    runCatching { track.stop() }
                    runCatching { track.release() }
                }
            }
        }
    }
}
