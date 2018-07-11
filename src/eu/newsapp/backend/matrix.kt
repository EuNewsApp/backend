/*
 backend
 Copyright (C) 2018 Dominic Meiser
 
 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/gpl-3.0>.
*/

package eu.newsapp.backend

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.*
import ch.qos.logback.core.UnsynchronizedAppenderBase
import de.msrd0.matrix.*
import de.msrd0.matrix.client.Room
import de.msrd0.matrix.client.event.*
import de.msrd0.matrix.client.event.TextMessageFormats.*
import java.io.*

internal lateinit var matrixRoom : Room

private fun createMessageContent(exception : Throwable?, message : String, context : String, level : Level) : MessageContent
{
	var text = ""
	var html = "<p>"
	
	val color = when (level) {
		Level.DEBUG -> "#22BB11"
		Level.WARN -> "#FFAA00"
		Level.ERROR -> "#EE0022"
		else -> "#1177FF"
	}
	
	text += "[${level.levelStr}]"
	html += "<span style=\"color:$color\" data-mx-color=\"$color\">[${level.levelStr}]</span>"
	
	text += " $context:\n"
	html += " <code>$context</code><br/>"
	
	text += message
	html += "$message</p>"
	
	if (exception != null)
	{
		val sw = StringWriter()
		exception.printStackTrace(PrintWriter(sw))
		
		text += "\n\n$sw"
		html += "\n<pre><code>$sw</code></pre>"
	}
	
	return FormattedTextMessageContent(text, HTML, html)
}

class MatrixAppender : UnsynchronizedAppenderBase<ILoggingEvent>()
{
	override fun append(event : ILoggingEvent?)
	{
		if (event == null || !::matrixRoom.isInitialized || event.level.levelInt < Level.WARN_INT)
			return
		
		val exception = event.throwableProxy.let { it as? ThrowableProxy }?.throwable
		matrixRoom.sendMessage(createMessageContent(exception, event.message, event.loggerName, event.level))
	}
}
