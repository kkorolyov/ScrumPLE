-- System schema init
CREATE SCHEMA IF NOT EXISTS System

CREATE TABLE IF NOT EXISTS System.event_codes (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  PRIMARY KEY (id))

CREATE TABLE IF NOT EXISTS System.projects (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  schema_name VARCHAR(64) NOT NULL,
  PRIMARY KEY (id))

CREATE TABLE IF NOT EXISTS System.sessions (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  source_4 CHAR(8) NULL,
  source_6 CHAR(32) NULL,
  start_date TIMESTAMP NOT NULL,
  project INT UNSIGNED NOT NULL,
  user_id INT UNSIGNED NOT NULL,
  active BIT(1) NOT NULL,
  PRIMARY KEY (id),
  INDEX project_idx (project ASC),
  INDEX user_id_idx (user_id ASC),
  CONSTRAINT project
    FOREIGN KEY (project)
    REFERENCES System.projects (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)

CREATE TABLE IF NOT EXISTS System.events (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  time_stamp TIMESTAMP NOT NULL,
  event_code INT UNSIGNED NOT NULL,
  description VARCHAR(256) NULL,
  session_id BIGINT UNSIGNED NOT NULL,
  INDEX event_idx (event_code ASC),
  PRIMARY KEY (id),
  INDEX session_id_idx (session_id ASC),
  CONSTRAINT event
    FOREIGN KEY (event_code)
    REFERENCES System.event_codes (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT session_id
    FOREIGN KEY (session_id)
    REFERENCES System.sessions (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
-- ----------------------------------------------------------------

-- Project schema init
CREATE SCHEMA IF NOT EXISTS Project

CREATE TABLE IF NOT EXISTS Project.roles (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	name VARCHAR(64) NOT NULL UNIQUE,
	PRIMARY KEY (id))
	
CREATE TABLE IF NOT EXISTS Project.users (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	credentials VARCHAR(128) NOT NULL,
	name VARCHAR(64) NOT NULL,
	role INT UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	INDEX role_idx (role ASC),
	CONSTRAINT role
		FOREIGN KEY (role)
		REFERENCES Project.roles (id)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION)
		
CREATE TABLE IF NOT EXISTS Project.labels (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	name VARCHAR(64) NOT NULL,
	PRIMARY KEY (id))
	
CREATE TABLE IF NOT EXISTS Project.releases (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	description VARCHAR(64) NOT NULL,
	start_date DATE NOT NULL,
	end_date DATE NOT NULL,
	PRIMARY KEY (id))
	
CREATE TABLE IF NOT EXISTS Project.sprints (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	description VARCHAR(64) NOT NULL,
	start_date DATE NOT NULL,
	end_date DATE NOT NULL,
	PRIMARY KEY (id))
	
CREATE TABLE IF NOT EXISTS Project.tasks (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	label INT UNSIGNED NOT NULL,
	description VARCHAR(256) NOT NULL,
	hours_left TINYINT NOT NULL,
	release_num INT UNSIGNED NULL,
	sprint INT UNSIGNED NULL,
	PRIMARY KEY (id),
	INDEX label_idx (label ASC),
	INDEX release_idx (release_num ASC),
	INDEX sprint_idx (sprint ASC),
	CONSTRAINT label
		FOREIGN KEY (label)
		REFERENCES Project.labels (id)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION,
	CONSTRAINT release_num
		FOREIGN KEY (release_num)
		REFERENCES Project.releases (id)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION,
	CONSTRAINT sprint
		FOREIGN KEY (sprint)
		REFERENCES Project.sprints (id)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION)
		
CREATE TABLE IF NOT EXISTS Project.issues (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	task INT UNSIGNED NOT NULL,
	description VARCHAR(256) NOT NULL,
	is_open BIT(1) NOT NULL,
	PRIMARY KEY (id),
	INDEX task_idx (task ASC),
	CONSTRAINT task
		FOREIGN KEY (task)
		REFERENCES Project.tasks (id)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION)
		
CREATE TABLE IF NOT EXISTS project (
	name VARCHAR(64) NOT NULL,
	description VARCHAR(256) NULL,
	PRIMARY KEY (name))
