CREATE TABLE IF NOT EXISTS Client(
id  INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
default_bank ENUM ('BTBANK', 'BROMBNAK', 'CITYBANK', 'INGBANK', 'BRDBANK'),
money_cash DECIMAL,
firstname VARCHAR(50),
lastname VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS App_User(
username  VARCHAR(50) NOT NULL PRIMARY KEY,
password NVARCHAR(512) NOT NULL,
client_id INT NOT NULL REFERENCES Client(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Operation(
id  INT AUTO_INCREMENT  PRIMARY KEY,
type  ENUM ('LOGIN','DEPOSIT','WITHDRAW','TRANSFER','CLOSEACCOUNT','BLOCKACCOUNT','UNBLOCKACCOUNT') NOT NULL,
user_detail  VARCHAR(50)  NOT NULL REFERENCES App_User(username) ON DELETE CASCADE,
time_stamp DATETIME
);

CREATE TABLE IF NOT EXISTS Login(
id  INT AUTO_INCREMENT PRIMARY KEY,
operation_id INT NOT NULL REFERENCES Operation(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Bank(
id  INT AUTO_INCREMENT PRIMARY KEY,
name ENUM('BTBANK', 'BROMBNAK', 'CITYBANK', 'INGBANK', 'BRDBANK')
);

CREATE TABLE IF NOT EXISTS Account(
iban  INT PRIMARY KEY,
bank_id INT  REFERENCES Bank(id) ON DELETE CASCADE,
type ENUM('CURRENT_ACCOUNT','SAVING_ACCOUNT') NOT NULL,
creating_date  DATETIME ,
closing_date DATETIME,
is_blocked BOOLEAN ,
is_closed BOOLEAN
);

CREATE TABLE IF NOT EXISTS Account_Measure(
id  INT AUTO_INCREMENT PRIMARY KEY,
operation_id INT  REFERENCES Operation(id) ON DELETE CASCADE,
account_iban INT  REFERENCES Account(iban) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Account_Transaction(
id  INT AUTO_INCREMENT PRIMARY KEY,
operation_id INT REFERENCES Operation(id) ON DELETE CASCADE,
account_iban INT  REFERENCES Account(iban) ON DELETE CASCADE,
account_receiver INT REFERENCES Account(iban) ON DELETE CASCADE,
amount DECIMAL NOT NULL
);

CREATE TABLE IF NOT EXISTS Account_Detail(
app_username VARCHAR(50) NOT NULL REFERENCES App_User(username) ON DELETE CASCADE,
account_iban INT NOT NULL REFERENCES Account(iban) ON DELETE CASCADE,
account_amount DECIMAL,
CONSTRAINT pk_user_account PRIMARY KEY (app_username,account_iban)
)
