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
  ('Daily Mail', 'UK', 'https://www.dailymail.co.uk/news/european-union/index.rss'),
  ('Daily Telegraph', 'UK', 'https://www.telegraph.co.uk/rss.xml'),
  ('La Repubblica', 'IT', 'http://www.repubblica.it/rss/homepage/rss2.0.xml'),
  ('Corriere della Sera', 'IT', 'http://xml2.corriereobjects.it/rss/politica.xml'),
  ('La Stammpa', 'IT', 'http://www.lastampa.it/italia/politica/rss.xml'),
  ('El Pais', 'ES', 'https://elpais.com/tag/rss/europa/a/'),
  ('El Mundo', 'ES', 'http://estaticos.elmundo.es/elmundo/rss/union_europea.xml'),
  ('ABC', 'ES', 'http://www.abc.es/rss/feeds/abc_Internacional.xml'),
  ('Rzeczpopspolita', 'PL', 'http://www.rp.pl/rss/11.html'),
  ('Adevarul', 'RO', 'http://adevarul.ro/rss/'),
  ('Realitatea', 'RO', 'http://rss.realitatea.net/extern.xml'),
  ('Evenimentul zilei', 'RO', 'http://evz.ro/rss'),
  ('De Telegraaf', 'NL', 'https://www.telegraaf.nl/nieuws/buitenland/rss'),
  ('De Volkskrant', 'NL','https://www.volkskrant.nl/nieuws-achtergrond/rss.xml'),
  ('Algemeen Dagblad','NL','https://www.ad.nl/politiek/rss.xml'),
  ('Le Soir', 'BG', 'http://www.lesoir.be/rss/81853/cible_principale_gratuit'),
  ('Kathimerini', 'GK', 'http://www.kathimerini.gr/rss'),
  ('Protothema', 'GK', 'https://www.protothema.gr/rss'),
  ('News.Gr', 'GK', 'https://www.news.gr/rss.ashx?catid=5')
ON CONFLICT DO NOTHING;

-- commit the transaction
COMMIT;
