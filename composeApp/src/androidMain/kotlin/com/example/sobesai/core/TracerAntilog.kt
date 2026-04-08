package com.example.sobesai.core

import io.github.aakira.napier.Antilog
import io.github.aakira.napier.LogLevel
import ru.ok.tracer.crash.report.TracerCrashReport

class TracerAntilog : Antilog() {
    override fun performLog(
        priority: LogLevel,
        tag: String?,
        throwable: Throwable?,
        message: String?
    ) {
        if (priority == LogLevel.ERROR && throwable != null) {
            TracerCrashReport.report(throwable)
        }
    }
}
