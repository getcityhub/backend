CREATE DATABASE cityhub;
USE cityhub;

CREATE TABLE topics (
  id INT NOT NULL AUTO_INCREMENT,
  name TEXT NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE posts (
  id INT NOT NULL AUTO_INCREMENT,
  author_id INT NOT NULL,
  title TEXT NOT NULL,
  text TEXT NOT NULL,
  topic_id INT NOT NULL,
  language CHAR(5) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

CREATE TABLE users (
  id INT NOT NULL AUTO_INCREMENT,
  first_name TEXT NOT NULL,
  last_name TEXT NOT NULL,
  anonymous TINYINT(1) NOT NULL,
  zipcode SMALLINT,
  languages TEXT NOT NULL,
  email TEXT NOT NULL,
  password TEXT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

CREATE TABLE politicians (
  id INT NOT NULL AUTO_INCREMENT,
  name TEXT NOT NULL,
  zipcodes TEXT NOT NULL,
  position TEXT,
  party TEXT,
  photo_url TEXT,
  email TEXT,
  phone TEXT,
  website TEXT,
  facebook TEXT,
  googleplus TEXT,
  twitter TEXT,
  youtube TEXT,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

CREATE TABLE translations (
  english TEXT NOT NULL,
  spanish TEXT,
  french TEXT,
  simplified TEXT,
  traditional TEXT
);

INSERT INTO translations VALUES ('Democrat', 'Demócrata', 'Démocrate', '民主党', '民主黨'),
    ('Republican', 'Republicano', 'Républicain', '共和党', '共和黨'),
    ('President', 'Presidente', 'Président', '总统', '總統'),
    ('Vice President', 'Vicepresidente', 'Vice-Président', '副总统', '副總統'),
    ('Senator', 'Senador', 'Sénateur', '参议员', '參議員'),
    ('Governor', 'Gobernador', 'Gouverneur', '州长', '州長'),
    ('Lieutenant Governor', 'Vicegobernador', 'Lieutenant Gouverneur', '副州长', '副州長'),
    ('Mayor', 'Alcalde', 'Maire', '市长', '市長'),
    ('Comptroller', 'Contraloría', 'Contrôleur', '审计官', '審計官'),
    ('Public Advocate', 'Defensor Publico', 'Défenseur public', '公众倡议者', '公眾倡議者'),
    ('Attorney General', 'Fiscal general', 'Avocat général', '司法部长', '司法部長'),
    ('State Comptroller', 'Anditor del estado', 'Contrôleur d\'état', '州审计长', '州審計長');
