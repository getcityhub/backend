CREATE DATABASE cityhub;
USE cityhub;

CREATE TABLE topics (
  id INT NOT NULL AUTO_INCREMENT,
  name TEXT NOT NULL,
  spanish TEXT,
  french TEXT,
  simplified TEXT,
  traditional TEXT,
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
  unique_code CHAR(8) NOT NULL,
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
