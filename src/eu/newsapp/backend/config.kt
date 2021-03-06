/*
 * backend
 * Copyright (C) 2018 Dominic Meiser
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl-3.0>.
 */

package eu.newsapp.backend

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

object Configuration
{
	private val logger : Logger = LoggerFactory.getLogger(Configuration::class.java)
	
	class Algolia
	{
		@JsonProperty
		var application = ""
		
		@JsonProperty
		var key = ""
		
		@JsonProperty
		var index = "articles"
	}
	
	class AWSKey
	{
		@JsonProperty
		var id = ""
		
		@JsonProperty
		var secret = ""
	}
	
	class AWS
	{
		@JsonProperty
		var key = AWSKey()
	}
	
	class Bugsnag
	{
		@JsonProperty
		var key = ""
		
		@JsonProperty
		var stage = "unknown"
	}
	
	class Database
	{
		@JsonProperty
		var host = "localhost"
		
		@JsonProperty
		var port = 5432
		
		@JsonProperty
		var name = "eunify"
		
		@JsonProperty
		var username = "postgres"
		
		@JsonProperty
		var password : String? = null
	}
	
	class ElasticSearch
	{
		@JsonProperty
		var host = "176.9.25.9"
		
		@JsonProperty
		var port = 9200
		
		@JsonProperty
		var username = "kibanaro"
		
		@JsonProperty
		var password = "kibanaro"
	}
	
	class Matrix
	{
		@JsonProperty
		var homeserver = "matrix.org"
		
		@JsonProperty
		var localpart = "eunify-bot"
		
		@JsonProperty
		var uri = "https://matrix.org/"
		
		@JsonProperty
		var token = ""
		
		@JsonProperty
		var device = ""
		
		@JsonProperty
		var room = ""
	}
	
	private class Root
	{
		@JsonProperty
		var algolia = Algolia()
		
		@JsonProperty
		var aws = AWS()
		
		@JsonProperty
		var bugsnag = Bugsnag()
		
		@JsonProperty
		var db = Database()
		
		@JsonProperty
		var elasticsearch = ElasticSearch()
		
		@JsonProperty
		var matrix = Matrix()
	}
	
	private var root = Root()
	
	val algolia get() = root.algolia
	val aws get() = root.aws
	val bugsnag get() = root.bugsnag
	val db get() = root.db
	val elasticsearch get() = root.elasticsearch
	val matrix get() = root.matrix
	
	/**
	 * Load the yaml configuration from [file].
	 */
	@JvmStatic
	fun loadConfig(file : File)
	{
		logger.info("Loading configuration from $file")
		val factory = YAMLFactory()
		val mapper = ObjectMapper(factory).registerKotlinModule()
		root = mapper.readValue(file, Root::class.java)
	}
	
	/**
	 * Load the yaml configuration from `/etc/eunify.yml`.
	 */
	@JvmOverloads
	@JvmStatic
	fun loadConfig(path : String? = null)
	{
		if (path == null)
		{
			val f = File("/etc/eunify.yml")
			if (f.exists())
				loadConfig(f)
		}
		else
			loadConfig(File(path))
	}
}
