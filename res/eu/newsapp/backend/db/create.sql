BEGIN;

-- Create the sources table

CREATE SEQUENCE IF NOT EXISTS "source_id_seq"
  INCREMENT 1
  MINVALUE 100
  CACHE 5
  NO CYCLE;

CREATE TABLE IF NOT EXISTS "source" (
  id BIGINT NOT NULL DEFAULT nextval('source_id_seq'),
  name TEXT NOT NULL,
  country CHARACTER(2) CHECK (upper(country) = country),
  "language" CHARACTER(2) CHECK (upper("language") = "language"),
  rss TEXT NOT NULL,
  -- primary key
	CONSTRAINT source_pkey PRIMARY KEY (id),
	-- unique constraints
	CONSTRAINT source_name_uniq UNIQUE (name)
);

ALTER SEQUENCE "source_id_seq" OWNED BY "source".id;

-- Create the articles table

CREATE SEQUENCE IF NOT EXISTS "article_id_seq"
	INCREMENT 1
	MINVALUE 10000
	CACHE 50
	NO CYCLE;

CREATE TABLE IF NOT EXISTS "article" (
	id BIGINT NOT NULL DEFAULT nextval('article_id_seq'),
	hash CHARACTER(64) NOT NULL,
	title TEXT NOT NULL,
	headline TEXT NOT NULL,
	source BIGINT NOT NULL,
	link TEXT NOT NULL,
	img TEXT,
	pub_date TIMESTAMP WITH TIME ZONE NOT NULL,
	title_en TEXT,
  headline_en TEXT,
	-- primary key
	CONSTRAINT article_pkey PRIMARY KEY (id),
	-- unique constraints
	CONSTRAINT article_source_hash_uniq UNIQUE (source, hash),
	CONSTRAINT article_link_uniq UNIQUE (link),
	-- foreign keys
	FOREIGN KEY (source) REFERENCES "source"(id) ON DELETE CASCADE
);

ALTER SEQUENCE "article_id_seq" OWNED BY "article".id;

-- commit transaction
COMMIT;
