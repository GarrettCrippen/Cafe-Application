COPY MENU
FROM 'C:\Users\playt\Desktop\Spring 2022\CS166\Projects\phase3\project-1\project\data\menu.csv'
WITH DELIMITER ';';

COPY USERS
FROM 'C:\Users\playt\Desktop\Spring 2022\CS166\Projects\phase3\project-1\project\data\users.csv'
WITH DELIMITER ';';

COPY ORDERS
FROM 'C:\Users\playt\Desktop\Spring 2022\CS166\Projects\phase3\project-1\project\data\orders.csv'
WITH DELIMITER ';';
ALTER SEQUENCE orders_orderid_seq RESTART 87257;

COPY ITEMSTATUS
FROM 'C:\Users\playt\Desktop\Spring 2022\CS166\Projects\phase3\project-1\project\data\itemStatus.csv'
WITH DELIMITER ';';

