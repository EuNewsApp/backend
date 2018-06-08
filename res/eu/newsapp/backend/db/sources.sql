BEGIN;

-- insert sources into the database

INSERT INTO "source" (name, country, rss) VALUES
  ('Spiegel', 'DE', 'https://www.spiegel.de/thema/eu/index.rss'),
  ('Süddeutsche', 'DE', 'https://www.sueddeutsche.de/news/rss?search=europa&sort=date&dep%5B%5D=politik&all%5B%5D=typ&all%5B%5D=sys&all%5B%5D=time'),
  ('Bild', 'DE', 'https://www.bild.de/rssfeeds/vw-politik/vw-politik-16728980,search=europa,dzbildplus=true,sort=1,teaserbildmobil=false,view=rss2.bild.xml'),
  ('Le Monde', 'FR', 'https://www.lemonde.fr/europe/rss_full.xml'),
  ('Le Figaro', 'FR', 'https://www.lefigaro.fr/rss/figaro_politique.xml'),
  ('Liberation', 'FR', 'https://rss.liberation.fr/rss/11/'),
  ('The Guardian', 'UK', 'https://www.theguardian.com/world/europe-news/rss'),
  ('Daily Mail', 'UK', 'https://www.dailymail.co.uk/news/european-union/index.rss')
ON CONFLICT DO NOTHING;

-- commit the transaction
COMMIT;
