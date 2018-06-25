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
  auto_scrape BOOLEAN NOT NULL,
  country CHARACTER(2) CHECK (upper(country) = country),
  language CHARACTER(2) CHECK (upper(language) = language),
  rss TEXT NOT NULL,
  -- primary key
  CONSTRAINT source_pkey PRIMARY KEY (id),
  -- unique constraints
  CONSTRAINT source_name_uniq UNIQUE (name),
  CONSTRAINT source_rss_uniq UNIQUE (rss)
);

ALTER SEQUENCE "source_id_seq" OWNED BY "source".id;

-- Create the articles table

CREATE SEQUENCE IF NOT EXISTS "article_id_seq"
  INCREMENT 1
  MINVALUE 100000
  CACHE 20
  NO CYCLE;

CREATE TABLE IF NOT EXISTS "article" (
  id BIGINT NOT NULL DEFAULT nextval('article_id_seq'),
  hash CHARACTER(64) NOT NULL CHECK (upper(hash) = hash),
  headline TEXT NOT NULL,
  teaser TEXT NOT NULL,
  source BIGINT NOT NULL,
  link TEXT NOT NULL,
  img TEXT,
  pub_date TIMESTAMP NOT NULL,
  is_translated BOOLEAN NOT NULL DEFAULT FALSE,
  is_classified BOOLEAN NOT NULL DEFAULT FALSE,
  is_published BOOLEAN NOT NULL DEFAULT FALSE,
  -- primary key
  CONSTRAINT article_pkey PRIMARY KEY (id),
  -- unique constraints
  CONSTRAINT article_source_hash_uniq UNIQUE (source, hash),
  CONSTRAINT article_link_uniq UNIQUE (link),
  -- foreign keys
  FOREIGN KEY (source) REFERENCES "source"(id) ON DELETE CASCADE
);

ALTER SEQUENCE "article_id_seq" OWNED BY "article".id;

-- Create the translations table

CREATE SEQUENCE IF NOT EXISTS "translation_id_seq"
  INCREMENT 1
  MINVALUE 1000000
  CACHE 30
  NO CYCLE;

CREATE TABLE IF NOT EXISTS "translation" (
  id BIGINT NOT NULL DEFAULT nextval('translation_id_seq'),
  article BIGINT NOT NULL,
  language CHARACTER(2) NOT NULL CHECK (upper(language) = language),
  headline TEXT NOT NULL,
  teaser TEXT NOT NULL,
  translated_at TIMESTAMP NOT NULL DEFAULT now(),
  -- primary key
  CONSTRAINT translation_pkey PRIMARY KEY (id),
  -- unique constraints
  CONSTRAINT translation_article_language_uniq UNIQUE (article, language),
  -- foreign keys
  FOREIGN KEY (article) REFERENCES "article"(id) ON DELETE CASCADE
);

ALTER SEQUENCE "translation_id_seq" OWNED BY "translation".id;

-- Create the classifications table

CREATE SEQUENCE IF NOT EXISTS "classification_id_seq"
  INCREMENT 1
  MINVALUE 5000000
  CACHE 30
  NO CYCLE;

CREATE TABLE IF NOT EXISTS "classification" (
  id BIGINT NOT NULL DEFAULT nextval('classification_id_seq'),
  article BIGINT NOT NULL,
  category CHARACTER VARYING(100) NOT NULL,
  classified_at TIMESTAMP NOT NULL DEFAULT now(),
  -- primary key
  CONSTRAINT classification_pkey PRIMARY KEY (id),
  -- unique constraints
  CONSTRAINT classification_article_category_uniq UNIQUE (article, category),
  -- foreign keys
  FOREIGN KEY (article) REFERENCES "article"(id) ON DELETE CASCADE
);

ALTER SEQUENCE "classification_id_seq" OWNED BY "classification".id;

-- Create the publications table

CREATE TABLE IF NOT EXISTS "publications" (
  article BIGINT NOT NULL,
  pub_id BIGINT NOT NULL,
  published_on TIMESTAMP NOT NULL DEFAULT now(),
  withdrawn_on TIMESTAMP DEFAULT NULL,
  -- primary key
  CONSTRAINT publications_pkey PRIMARY KEY (article),
  -- unique constraints
  CONSTRAINT publications_article_uniq UNIQUE (article),
  CONSTRAINT publications_pub_id_uniq UNIQUE (pub_id),
  -- foreign keys
  FOREIGN KEY (article) REFERENCES "article"(id) ON DELETE CASCADE
);

-- Create the log id sequence

CREATE SEQUENCE IF NOT EXISTS "log_seq"
  INCREMENT 1
  MINVALUE 10000000
  CACHE 40
  NO CYCLE;

-- Create the log tables

CREATE TABLE IF NOT EXISTS "scrape_log" (
  id BIGINT NOT NULL DEFAULT nextval('log_seq'),
  timestamp TIMESTAMP NOT NULL DEFAULT now(),
  source BIGINT NOT NULL,
  articles_total INTEGER NOT NULL,
  articles_included INTEGER NOT NULL,
  articles_new INTEGER NOT NULL,
  -- primary key
  CONSTRAINT scrape_log_pkey PRIMARY KEY (id),
  -- foreign keys
  FOREIGN KEY (source) REFERENCES "source"(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "translation_log" (
  id BIGINT NOT NULL DEFAULT nextval('log_seq'),
  timestamp TIMESTAMP NOT NULL DEFAULT now(),
  language_from CHARACTER(2) CHECK (upper(language_from) = language_from),
  language_to CHARACTER(2) CHECK (upper(language_to) = language_to),
  character_count INTEGER NOT NULL,
  -- primary key
  CONSTRAINT translation_log_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS "classification_log" (
  id BIGINT NOT NULL DEFAULT nextval('log_seq'),
  timestamp TIMESTAMP NOT NULL DEFAULT now(),
  character_count INTEGER NOT NULL,
  -- primary key
  CONSTRAINT classification_log_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS "publication_log" (
  id BIGINT NOT NULL DEFAULT nextval('log_seq'),
  timestamp TIMESTAMP NOT NULL DEFAULT now(),
  chunk_count INTEGER NOT NULL,
  -- primary key
  CONSTRAINT publication_log_pkey PRIMARY KEY (id)
);

-- commit transaction
COMMIT;
