---------------------------------------------------------------
--- Run using user with system admin privileges.            ---
--- Will create new database, two logins and two users.     ---
---------------------------------------------------------------

USE master;
IF NOT EXISTS(SELECT * FROM master.dbo.syslogins WHERE NAME = N'vr')
  CREATE LOGIN vr WITH PASSWORD=N'vr1234', CHECK_EXPIRATION=OFF, CHECK_POLICY=OFF
-- login vr is shared between multiple users, so drop will often fail.
--EXEC sp_droplogin 'vr'

IF NOT EXISTS(SELECT * FROM master.dbo.syslogins WHERE NAME = N'vrAdmin')
  CREATE LOGIN vrAdmin WITH PASSWORD=N'vrAdmin1234', CHECK_EXPIRATION=OFF, CHECK_POLICY=OFF
-- login vrAdmin is shared between multiple users, so drop will often fail.
--EXEC sp_droplogin 'vrAdmin'

IF EXISTS(SELECT * FROM sysdatabases WHERE NAME = N'ValueReporter')
  BEGIN
    ALTER DATABASE "ValueReporter" SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE "ValueReporter";
  END;
CREATE DATABASE "ValueReporter";
GO

USE "ValueReporter";
CREATE USER vrAdmin FOR LOGIN vrAdmin;
EXEC sp_addrolemember 'db_owner', 'vrAdmin'

CREATE USER vr FOR LOGIN vr;
EXEC sp_addrolemember 'db_datareader', 'vr'
EXEC sp_addrolemember 'db_datawriter', 'vr'
GO