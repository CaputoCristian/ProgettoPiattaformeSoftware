-- DROP SCHEMA orders;
-- CREATE SCHEMA orders;
-- USE orders;

CREATE TABLE user (
	id INTEGER AUTO_INCREMENT PRIMARY KEY,
	cf VARCHAR(16),
	first_name VARCHAR(50),
	last_name VARCHAR(50),
	telephone_number VARCHAR(20),
	email VARCHAR(90),
	address VARCHAR(150),
    birth_date DATE
);

CREATE TABLE shop (     --Ogni utente può mettere in vendita i propri prodotti
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    seller INTEGER,
    FOREIGN KEY (seller) REFERENCES user (id)
);

CREATE TABLE product (
	id INTEGER AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(50),
    brand VARCHAR(50),
    product_type VARCHAR(50), --Ricerca per tipologia (tipi predefiniti) //TODO
    description VARCHAR(500),
	price FLOAT, --Usare DECIMAL? Evita problemi di precisione
    quantity INTEGER,
    sold_by INTEGER,
    FOREIGN KEY (sold_by) REFERENCES shop (id)
);

CREATE TABLE purchase (
	id INTEGER AUTO_INCREMENT PRIMARY KEY,
	buyer INTEGER,
	purchase_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (buyer) REFERENCES user (id)
);

CREATE TABLE product_in_purchase ( --Separato da purchase per gestire acquisti con più prodotti
	id INTEGER AUTO_INCREMENT PRIMARY KEY,
	related_purchase INTEGER,
	product INTEGER,
    quantity INTEGER,
    FOREIGN KEY (related_purchase) REFERENCES purchase (id),
    FOREIGN KEY (product) REFERENCES product (id)
);

CREATE TABLE review (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    user_id INTEGER,
    purchase_id INTEGER,
    product_id INTEGER,
    vote  INTEGER, CHECK (vote >= 0 AND vote <= 5),
    message VARCHAR(500),
    --buyer INTEGER,
    --FOREIGN KEY (buyer) REFERENCES purchase (id),
    FOREIGN KEY (user_id) REFERENCES user (id),
    FOREIGN KEY (purchase_id) REFERENCES purchase (id),
    FOREIGN KEY (product_id) REFERENCES product (id)
);
-- TODO Chat e proposte di prezzo?