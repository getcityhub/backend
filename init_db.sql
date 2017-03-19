CREATE DATABASE cityhub;
USE cityhub;

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

CREATE TABLE likes (
  user_id INT NOT NULL,
  post_id INT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE events (
  id INT NOT NULL AUTO_INCREMENT,
  host TEXT NOT NULL,
  name TEXT NOT NULL,
  description TEXT NOT NULL,
  start_date DATETIME NOT NULL,
  end_date DATETIME NOT NULL,
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
  verified TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (id)
);

CREATE TABLE verification_codes (
  email TEXT,
  code TEXT
);

CREATE TABLE politicians (
  id INT NOT NULL AUTO_INCREMENT,
  name TEXT NOT NULL,
  male TINYINT(1) NOT NULL DEFAULT 1,
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
  language TEXT NOT NULL,
  text TEXT NOT NULL,
  reason_id INT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

CREATE TABLE password_reset_requests (
  id INT NOT NULL AUTO_INCREMENT,
  user_id INT NOT NULL,
  code TEXT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

CREATE TABLE mailing_list (
  email TEXT
);

CREATE TABLE mailing_list_unconfirmed (
  email TEXT,
  code TEXT
);

CREATE TABLE translations (
  english TEXT NOT NULL,
  spanish TEXT,
  french TEXT,
  simplified TEXT,
  traditional TEXT
);

INSERT INTO translations VALUES ('Democrat', 'Demócrata', 'Démocrate', '民主党', '民主黨'),
    ('Republican', 'Republicano', 'Républicain(e)', '共和党', '共和黨'),
    ('President', 'Presidente', '(Madame la )Président(e)', '总统', '總統'),
    ('Vice President', 'Vicepresidente', 'Vice-Président(e)', '副总统', '副總統'),
    ('Senator', 'Senador(a)', 'Sénat(eur/rice)', '参议员', '參議員'),
    ('Governor', 'Gobernador(a)', 'Gouverneur', '州长', '州長'),
    ('Lieutenant Governor', 'Vicegobernador(a)', 'Lieutenant Gouverneur', '副州长', '副州長'),
    ('Mayor', 'Alcalde(sa)', 'Maire', '市长', '市長'),
    ('Comptroller', 'Contraloría', 'Contrôleur', '审计官', '審計官'),
    ('Public Advocate', 'Defensor(a) Public(o/a)', 'Défenseur public', '公众倡议者', '公眾倡議者'),
    ('Bronx District Attorney',	'Fiscal de distrito del Bronx',	'Avocat(e) de Bronx', '布朗克斯地方检察官',	'布朗克斯地方檢察官'),
    ('County District Attorney', 'Fiscal de distrito del Condado', 'Avocat(e) du comté', '地方检察官', '地方檢察官'),
    ('Manhattan District Attorney',	'Fiscal de distrito del Manhattan',	'Avocat(e) de Manhattan', '曼哈顿地方检察官', '曼哈顿地方檢察官'),
    ('Queens District Attorney', 'Fiscal de distrito del Queens', 'Avocat(e) de Queens', '皇后区地方检察官', '皇后區地方檢察官'),
    ('Richmond District Attorney', 'Fiscal de distrito del Richmond', 'Avocat(e) de Richmond',	'史丹顿岛地方检察官', '史丹頓島地方檢察官'),
    ('Bronx Borough President',	'President(e/a) del condado del Bronx',	'Président(e) d''arrondissement de Bronx',	'布朗克斯区长', '布朗克斯區長'),
    ('Brooklyn Borough President', 'President(e/a) del condado del Brooklyn', 'Président(e) d''arrondissement de Brooklyn', '布鲁克林区长',	'布魯克林區長'),
    ('Manhattan Borough President', 'President(e/a) del condado del Manhattan', 'Président(e) d''arrondissement de Manhattan', '曼哈顿区长', '曼哈頓區長'),
    ('Queens Borough President', 'President(e/a) del condado del Queens', 'Président(e) d''arrondissement de Queens', '皇后区区长',	'皇后區區長'),
    ('Staten Island Borough President',	'President(e/a) del condado del Staten Island',	'Président(e) d''arrondissement de Staten Island',	'史丹顿岛区长', '史丹頓島區張'),
    ('Attorney General', 'Fiscal general', 'Avocat(e) général', '司法部长', '司法部長'),
    ('State Comptroller', 'Auditor del estado', 'Contrôleur d''état', '州审计长', '州審計長'),
    ('NY-03 Representative', 'Representante NY-03', 'Représentant(e) NY-03', 'NY-03 议员', 'NY-03 议员'),
    ('NY-05 Representative', 'Representante NY-05', 'Représentant(e) NY-05', 'NY-05 议员', 'NY-05 议员'),
    ('NY-06 Representative', 'Representante NY-06', 'Représentant(e) NY-06', 'NY-06 议员', 'NY-06 议员'),
    ('NY-07 Representative', 'Representante NY-07', 'Représentant(e) NY-07', 'NY-07 议员', 'NY-07 议员'),
    ('NY-08 Representative', 'Representante NY-08', 'Représentant(e) NY-08', 'NY-08 议员', 'NY-08 议员'),
    ('NY-09 Representative', 'Representante NY-09', 'Représentant(e) NY-09', 'NY-09 议员', 'NY-09 议员'),
    ('NY-10 Representative', 'Representante NY-10', 'Représentant(e) NY-10', 'NY-10 议员', 'NY-10 议员'),
    ('NY-11 Representative', 'Representante NY-11', 'Représentant(e) NY-11', 'NY-11 议员', 'NY-11 议员'),
    ('NY-12 Representative', 'Representante NY-12', 'Représentant(e) NY-12', 'NY-12 议员', 'NY-12 议员'),
    ('NY-13 Representative', 'Representante NY-13', 'Représentant(e) NY-13', 'NY-13 议员', 'NY-13 议员'),
    ('NY-14 Representative', 'Representante NY-14', 'Représentant(e) NY-14', 'NY-14 议员', 'NY-14 议员'),
    ('NY-15 Representative', 'Representante NY-15', 'Représentant(e) NY-15', 'NY-15 议员', 'NY-15 议员'),
    ('NY-16 Representative', 'Representante NY-16', 'Représentant(e) NY-16', 'NY-16 议员', 'NY-16 议员'),
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