package com.flexify.app.domain.manager

import com.flexify.app.domain.parser.LogLine

actual class LogManager {
	actual val logs: List<LogLine>
		get() = TODO()
	actual fun startStreaming() {}
	actual fun stopStreaming() {}
	actual fun clearLogs() {}
}
