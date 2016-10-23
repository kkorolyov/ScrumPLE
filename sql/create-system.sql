-- System schema init
CREATE TABLE IF NOT EXISTS event_codes (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  PRIMARY KEY (id))

CREATE TABLE IF NOT EXISTS projects (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  schema_name VARCHAR(64) NOT NULL,
  PRIMARY KEY (id))

CREATE TABLE IF NOT EXISTS sessions (
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
    REFERENCES projects (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)

CREATE TABLE IF NOT EXISTS events (
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
    REFERENCES event_codes (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT session_id
    FOREIGN KEY (session_id)
    REFERENCES sessions (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
