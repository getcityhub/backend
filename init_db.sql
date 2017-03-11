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
  language TEXT NOT NULL,
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

CREATE TABLE reports (
  id INT NOT NULL AUTO_INCREMENT,
  reporter_id INT NOT NULL,
  text TEXT NOT NULL,
  reason INT NOT NULL,
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
    ('Bronx District Attorney',	'Fiscal de distrito del Bronx',	'Avocat de Bronx', '布朗克斯地方检察官',	'布朗克斯地方檢察官'),
    ('County District Attorney', 'Fiscal de distrito del Condado', 'Avocat du comté', '地方检察官', '地方檢察官'),
    ('Manhattan District Attorney',	'Fiscal de distrito del Manhattan',	'Avocat de Manhattan', '曼哈顿地方检察官', '曼哈顿地方檢察官'),
    ('Queens District Attorney', 'Fiscal de distrito del Queens', 'Avocat de Queens', '皇后区地方检察官', '皇后區地方檢察官'),
    ('Richmond District Attorney', 'Fiscal de distrito del Richmond', 'Avocat de Richmond',	'史丹顿岛地方检察官', '史丹頓島地方檢察官'),
    ('Bronx Borough President',	'Presidente del condado del Bronx',	'Président d''arrondissement de Bronx',	'布朗克斯区长', '布朗克斯區長'),
    ('Brooklyn Borough President', 'Presidente del condado del Brooklyn', 'Président d''arrondissement de Brooklyn', '布鲁克林区长',	'布魯克林區長'),
    ('Manhattan Borough President', 'Presidente del condado del Manhattan', 'Président d''arrondissement de Manhattan', '曼哈顿区长', '曼哈頓區長'),
    ('Queens Borough President', 'Presidente del condado del Queens', 'Président d''arrondissement de Queens', '皇后区区长',	'皇后區區長'),
    ('Staten Island Borough President',	'Presidente del condado del Staten Island',	'Président d''arrondissement de Staten Island',	'史丹顿岛区长', '史丹頓島區張'),
    ('Attorney General', 'Fiscal general', 'Avocat général', '司法部长', '司法部長'),
    ('State Comptroller', 'Anditor del estado', 'Contrôleur d''état', '州审计长', '州審計長'),
    -- NY-XX representative needed
    ('Public Health/Safety', 'La salud y seguridad publica', 'Santé publique/Sécurité',	'公共安全与卫生', '公共安全與衛生'),
	('Transportation', 'Transportación', 'Transport', '交通', '交通'),
	('Vehicles and Parking', 'Vehículos y Aparacar', 'Véhicules et Parking', '车辆停车',	'車輛停車'),
	('Taxes', 'Impuestos', 'Taxes', '税务', '稅務'),
	('Noise', 'Ruido', 'Bruit',	'噪声',	'噪聲'),
	('Business', 'Negocio',	'Entreprise', '商业', '商業'),
	('Education', 'Educación', 'Éducacion',	'教育', '教育'),
	('Civic Services', 'Servicios civicos',	'Les services civiques', '市民服务', '市民服務'),
	('Housing and Development',	'Desarrollo de vivienda', 'Developpement du logement', '住宅开发', '住宅開發'),
	('Recreation', 'Recreacion', 'Récréation', '文化与娱乐', '文化與娛樂'),
	('Social Services',	'Servicios sociales', 'Services sociaux', '社会服务',	'社會服務');
