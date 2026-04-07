package com.example.sobesai

import android.app.Application
import com.example.sobesai.core.utils.TracerAntilog
import com.example.sobesai.di.appModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.ok.tracer.CoreTracerConfiguration
import ru.ok.tracer.HasTracerConfiguration
import ru.ok.tracer.TracerConfiguration
import ru.ok.tracer.crash.report.CrashReportConfiguration

class MainApplication : Application(), HasTracerConfiguration {
    override val tracerConfiguration: List<TracerConfiguration>
        get() = listOf(
            CoreTracerConfiguration.build {
                setDebugUpload(true)
            },
            CrashReportConfiguration.build {
            }
        )

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(appModule)
        }
        Napier.base(DebugAntilog())
        Napier.base(TracerAntilog())
    }
}
