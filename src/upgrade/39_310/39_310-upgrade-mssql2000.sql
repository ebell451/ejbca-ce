EXEC sp_rename 'LogEntryData.[comment]', 'logComment', 'COLUMN';
ALTER TABLE TableProtectData DROP COLUMN keyRef;
ALTER TABLE CertificateData ADD subjectKeyId VARCHAR(256) DEFAULT NULL;
