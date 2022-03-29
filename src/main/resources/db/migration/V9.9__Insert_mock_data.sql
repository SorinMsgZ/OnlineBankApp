insert into Client (id, default_bank, money_cash, firstname, lastname) values (1, 'BTBANK', 9.28,'alex','alex');
insert into Client (id, default_bank, money_cash, firstname, lastname) values (2, 'BTBANK', 3.40,'volodimir','Nume2');
insert into Client (id, default_bank, money_cash, firstname, lastname) values (3, 'BTBANK', 0.60,'Prenume3','Nume3');
insert into Client (id, default_bank, money_cash, firstname, lastname) values (4, 'BTBANK', 0.19,'Prenume4','Nume4');
insert into Client (id, default_bank, money_cash, firstname, lastname) values (5, 'BTBANK', 5.72,'Prenume5','Nume5');

insert into App_User (username, password, client_id) values ('alex', '{bcrypt}$2a$12$ewr3FVyJy4/Sd6s2vvQhJ.Iclu80LbDWRv9Do6UZbKsXf835Vnw66', 1);
insert into App_User (username, password, client_id) values ('volodimir', '$2a$12$ewr3FVyJy4/Sd6s2vvQhJ.Iclu80LbDWRv9Do6UZbKsXf835Vnw66', 2);
insert into App_User (username, password, client_id) values ('hans', '$2a$12$ewr3FVyJy4/Sd6s2vvQhJ.Iclu80LbDWRv9Do6UZbKsXf835Vnw66', 3);
insert into App_User (username, password, client_id) values ('ionel', '$2a$12$ewr3FVyJy4/Sd6s2vvQhJ.Iclu80LbDWRv9Do6UZbKsXf835Vnw66', 4);
insert into App_User (username, password, client_id) values ('fakeuser', '$2a$12$ewr3FVyJy4/Sd6s2vvQhJ.Iclu80LbDWRv9Do6UZbKsXf835Vnw66', 5);

insert into Operation (id, type, user_detail,time_stamp) values (1, 'LOGIN', 'volodimir','2021-02-21 12:30:00.000');
insert into Operation (id, type, user_detail,time_stamp) values (2, 'DEPOSIT', 'alex','2021-02-21 12:30:00.000');
insert into Operation (id, type, user_detail,time_stamp) values (3, 'WITHDRAW', 'alex','2021-02-21 12:30:00.000');
insert into Operation (id, type, user_detail,time_stamp) values (4, 'TRANSFER', 'ionel','2021-02-21 12:30:00.000');
insert into Operation (id, type, user_detail,time_stamp) values (5, 'CLOSEACCOUNT', 'ionel','2021-02-21 12:30:00.000');
insert into Operation (id, type, user_detail,time_stamp) values (6, 'BLOCKACCOUNT', 'ionel','2021-02-21 12:30:00.000');
insert into Operation (id, type, user_detail,time_stamp) values (7, 'UNBLOCKACCOUNT', 'ionel','2021-02-21 12:30:00.000');

insert into Login (id, operation_id) values (1, 1);

insert into Bank (id, name) values (1, 'BTBANK');
insert into Bank (id, name) values (2, 'BROMBNAK');
insert into Bank (id, name) values (3, 'CITYBANK');
insert into Bank (id, name) values (4, 'INGBANK');
insert into Bank (id, name) values (5, 'BRDBANK');

insert into Account (iban, bank_id, type, creating_date, closing_date, is_blocked, is_closed) values (1, 1, 'CURRENT_ACCOUNT', '2021-02-21 12:30:00.000', null, 0, 0);
insert into Account (iban, bank_id, type, creating_date, closing_date, is_blocked, is_closed) values (2, 1, 'SAVING_ACCOUNT', '2021-02-21 12:30:00.000', null, 0, 0);
insert into Account (iban, bank_id, type, creating_date, closing_date, is_blocked, is_closed) values (3, 1, 'CURRENT_ACCOUNT', '2021-02-21 12:30:00.000', null, 0, 0);
insert into Account (iban, bank_id, type, creating_date, closing_date, is_blocked, is_closed) values (4, 1, 'CURRENT_ACCOUNT', '2021-02-21 12:30:00.000', null, 0, 0);
insert into Account (iban, bank_id, type, creating_date, closing_date, is_blocked, is_closed) values (5, 1, 'CURRENT_ACCOUNT', '2021-02-21 12:30:00.000', null, 0, 0);
insert into Account (iban, bank_id, type, creating_date, closing_date, is_blocked, is_closed) values (6, 1, 'CURRENT_ACCOUNT', '2021-02-21 12:30:00.000', null, 0, 0);
insert into Account (iban, bank_id, type, creating_date, closing_date, is_blocked, is_closed) values (7, 1, 'CURRENT_ACCOUNT', '2021-02-21 12:30:00.000', null, 0, 0);
insert into Account (iban, bank_id, type, creating_date, closing_date, is_blocked, is_closed) values (8, 2, 'CURRENT_ACCOUNT', '2021-02-21 12:30:00.000', null, 0, 0);

insert into Account_Measure (id, operation_id, account_iban) values (1, 1, 1);
insert into Account_Measure (id, operation_id, account_iban) values (2, 2, 2);
insert into Account_Measure (id, operation_id, account_iban) values (3, 3, 3);
insert into Account_Measure (id, operation_id, account_iban) values (4, 4, 4);
insert into Account_Measure (id, operation_id, account_iban) values (5, 5, 5);
insert into Account_Measure (id, operation_id, account_iban) values (6, 6, 6);
insert into Account_Measure (id, operation_id, account_iban) values (7, 7, 7);

insert into Account_Transaction (id, operation_id, account_iban, account_receiver, amount) values (1, 1, 1, 7, 66);
insert into Account_Transaction (id, operation_id, account_iban, account_receiver, amount) values (2, 2, 2, 6, 29);
insert into Account_Transaction (id, operation_id, account_iban, account_receiver, amount) values (3, 3, 3, 5, 89);
insert into Account_Transaction (id, operation_id, account_iban, account_receiver, amount) values (4, 4, 4, 3, 53);
insert into Account_Transaction (id, operation_id, account_iban, account_receiver, amount) values (5, 5, 5, 4, 36);
insert into Account_Transaction (id, operation_id, account_iban, account_receiver, amount) values (6, 6, 6, 2, 88);
insert into Account_Transaction (id, operation_id, account_iban, account_receiver, amount) values (7, 7, 7, 1, 4);

insert into Account_Detail (app_username, account_iban, account_amount) values ('alex', 1, 1);
insert into Account_Detail (app_username, account_iban, account_amount) values ('volodimir', 2, 2);
insert into Account_Detail (app_username, account_iban, account_amount) values ('hans', 3, 3);
insert into Account_Detail (app_username, account_iban, account_amount) values ('ionel', 4, 4);
insert into Account_Detail (app_username, account_iban, account_amount) values ('fakeuser', 5, 5);
insert into Account_Detail (app_username, account_iban, account_amount) values ('alex', 6, 6);
insert into Account_Detail (app_username, account_iban, account_amount) values ('alex', 7, 7);