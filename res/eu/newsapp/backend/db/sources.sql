BEGIN;

-- insert sources into the database

INSERT INTO "source" (name, auto_scrape, country, "language", rss) VALUES
  ('Spiegel', TRUE, 'DE', 'DE', 'https://www.spiegel.de/thema/eu/index.rss'),
  ('Süddeutsche', TRUE, 'DE', 'DE', 'https://www.sueddeutsche.de/news/rss?search=europa&sort=date&dep%5B%5D=politik&all%5B%5D=typ&all%5B%5D=sys&all%5B%5D=time'),
  ('Bild', FALSE, 'DE', 'DE', 'https://www.bild.de/rssfeeds/vw-politik/vw-politik-16728980,search=europa,dzbildplus=true,sort=1,teaserbildmobil=false,view=rss2.bild.xml'),
  ('Le Monde', TRUE, 'FR', 'FR', 'https://www.lemonde.fr/europe/rss_full.xml'),
  ('Le Figaro', TRUE, 'FR', 'FR', 'https://www.lefigaro.fr/rss/figaro_politique.xml'),
  ('The Guardian', TRUE, 'UK', 'EN', 'https://www.theguardian.com/world/europe-news/rss'),
  ('BBC', TRUE, 'UK', 'EN', 'http://feeds.bbci.co.uk/news/world/europe/rss.xml'),
  ('Daily Telegraph', TRUE, 'UK', 'EN', 'https://www.telegraph.co.uk/rss.xml'),
  ('La Repubblica', FALSE, 'IT', 'IT', 'http://www.repubblica.it/rss/homepage/rss2.0.xml'),
  ('Corriere della Sera', FALSE, 'IT', 'IT', 'http://xml2.corriereobjects.it/rss/politica.xml'),
  ('La Stampa', FALSE, 'IT', 'IT', 'http://www.lastampa.it/italia/politica/rss.xml'),
  ('El Pais', TRUE, 'ES', 'ES', 'https://elpais.com/tag/rss/europa/a/'),
  ('El Mundo', TRUE, 'ES', 'ES', 'http://estaticos.elmundo.es/elmundo/rss/union_europea.xml'),
  ('ABC', TRUE, 'ES', 'ES', 'http://www.abc.es/rss/feeds/abc_Internacional.xml'),
  ('Rzeczpopspolita', FALSE, 'PL', 'PL', 'http://www.rp.pl/rss/11.html'),
  ('Adevarul', FALSE, 'RO', 'RO', 'http://adevarul.ro/rss/'),
  ('Realitatea', FALSE, 'RO', 'RO', 'http://rss.realitatea.net/extern.xml'),
  ('Evenimentul zilei', FALSE, 'RO', 'RO', 'http://evz.ro/rss'),
  ('De Telegraaf', FALSE, 'NL', 'NL', 'https://www.telegraaf.nl/nieuws/buitenland/rss'),
  ('De Volkskrant', FALSE, 'NL', 'NL', 'https://www.volkskrant.nl/nieuws-achtergrond/rss.xml'),
  ('Algemeen Dagblad', FALSE, 'NL', 'NL', 'https://www.ad.nl/politiek/rss.xml'),
  ('Le Soir', TRUE, 'BG', 'FR', 'http://www.lesoir.be/rss/81853/cible_principale_gratuit'),
  ('Kathimerini', FALSE, 'GR', 'GR', 'http://www.kathimerini.gr/rss'),
  ('Protothema', FALSE, 'GR', 'GR', 'https://www.protothema.gr/rss'),
  ('News.Gr', FALSE, 'GR', 'GR', 'https://www.news.gr/rss.ashx?catid=5'),
  ('Jornal de Notícias', TRUE, 'PT', 'PT', 'http://feeds.jn.pt/JN-Mundo'),
  ('Observador', TRUE, 'PT', 'PT', 'https://observador.pt/feed/')
ON CONFLICT DO NOTHING;

-- commit the transaction
COMMIT;
