BEGIN;

-- insert sources into the database

INSERT INTO "source" (name, country, "language", rss) VALUES
  ('Spiegel', 'DE', 'DE', 'https://www.spiegel.de/thema/eu/index.rss'),
  ('Süddeutsche', 'DE', 'DE', 'https://www.sueddeutsche.de/news/rss?search=europa&sort=date&dep%5B%5D=politik&all%5B%5D=typ&all%5B%5D=sys&all%5B%5D=time'),
  ('Bild', 'DE', 'DE', 'https://www.bild.de/rssfeeds/vw-politik/vw-politik-16728980,search=europa,dzbildplus=true,sort=1,teaserbildmobil=false,view=rss2.bild.xml'),
  ('Le Monde', 'FR', 'FR', 'https://www.lemonde.fr/europe/rss_full.xml'),
  ('Le Figaro', 'FR', 'FR', 'https://www.lefigaro.fr/rss/figaro_politique.xml'),
  ('Liberation', 'FR', 'FR', 'https://rss.liberation.fr/rss/11/'),
  ('The Guardian', 'UK', 'EN', 'https://www.theguardian.com/world/europe-news/rss'),
  ('Daily Mail', 'UK', 'EN', 'https://www.dailymail.co.uk/news/european-union/index.rss'),
  ('Daily Telegraph', 'UK', 'EN', 'https://www.telegraph.co.uk/rss.xml'),
  ('La Repubblica', 'IT', 'IT', 'http://www.repubblica.it/rss/homepage/rss2.0.xml'),
  ('Corriere della Sera', 'IT', 'IT', 'http://xml2.corriereobjects.it/rss/politica.xml'),
  ('La Stammpa', 'IT', 'IT', 'http://www.lastampa.it/italia/politica/rss.xml'),
  ('El Pais', 'ES', 'ES', 'https://elpais.com/tag/rss/europa/a/'),
  ('El Mundo', 'ES', 'ES', 'http://estaticos.elmundo.es/elmundo/rss/union_europea.xml'),
  ('ABC', 'ES', 'ES', 'http://www.abc.es/rss/feeds/abc_Internacional.xml'),
  ('Rzeczpopspolita', 'PL', 'PL', 'http://www.rp.pl/rss/11.html'),
  ('Adevarul', 'RO', 'RO', 'http://adevarul.ro/rss/'),
  ('Realitatea', 'RO', 'RO', 'http://rss.realitatea.net/extern.xml'),
  ('Evenimentul zilei', 'RO', 'RO', 'http://evz.ro/rss'),
  ('De Telegraaf', 'NL', 'NL', 'https://www.telegraaf.nl/nieuws/buitenland/rss'),
  ('De Volkskrant', 'NL', 'NL','https://www.volkskrant.nl/nieuws-achtergrond/rss.xml'),
  ('Algemeen Dagblad','NL', 'NL','https://www.ad.nl/politiek/rss.xml'),
  ('Le Soir', 'BG', 'FR', 'http://www.lesoir.be/rss/81853/cible_principale_gratuit'),
  ('Kathimerini', 'GR', 'GR', 'http://www.kathimerini.gr/rss'),
  ('Protothema', 'GR', 'GR', 'https://www.protothema.gr/rss'),
  ('News.Gr', 'GR', 'GR', 'https://www.news.gr/rss.ashx?catid=5')
ON CONFLICT DO NOTHING;

-- commit the transaction
COMMIT;
