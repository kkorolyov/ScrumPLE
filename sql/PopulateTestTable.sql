CREATE TABLE test (
	isChar CHAR(1) NOT NULL,
	isVarchar VARCHAR(16),
	isInt INT UNSIGNED NOT NULL PRIMARY KEY,
	autoInc INT UNSIGNED NOT NULL AUTO_INCREMENT
)

-- This is a comment

INSERT INTO test (isChar, isVarchar, isInt)
VALUES ('T', 'Test', 4)