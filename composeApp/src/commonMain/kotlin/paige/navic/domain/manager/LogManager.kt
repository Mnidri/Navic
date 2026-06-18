package com.flexify.app.domain.manager

import com.flexify.app.domain.parser.LogLine

expect class LogManager {
	val logs: List<LogLine>
	fun startStreaming()
	fun stopStreaming()
	fun clearLogs()
}
