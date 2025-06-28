DROP SCHEMA orders;
CREATE SCHEMA orders;
USE orders;

CREATE TABLE app_user (
	id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
/*  cf VARCHAR(16),   */
	first_name VARCHAR(50),
	last_name VARCHAR(50),
	telephone_number VARCHAR(20),
	email VARCHAR(90),
	address VARCHAR(150),
    birth_date DATE NULL
);

CREATE TABLE shop (     /*Ogni utente può mettere in vendita i propri prodotti*/
    id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
    seller INTEGER,
    FOREIGN KEY (seller) REFERENCES app_user (id)
);

CREATE TABLE product (
	id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
	name VARCHAR(50),
    brand VARCHAR(50),
    product_type VARCHAR(50), /*Ricerca per tipologia (tipi predefiniti) */
    description VARCHAR(500),
	price FLOAT, /*Usare DECIMAL? Evita problemi di precisione*/
    quantity INTEGER,
    sold_by INTEGER,
    FOREIGN KEY (sold_by) REFERENCES shop (id)
);

CREATE TABLE purchase (
	id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
	buyer INTEGER,
	time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (buyer) REFERENCES app_user (id)
);

CREATE TABLE product_in_purchase ( /*Separato da purchase per gestire acquisti con più prodotti*/
	id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
	related_purchase INTEGER,
	product INTEGER,
    quantity INTEGER,
    FOREIGN KEY (related_purchase) REFERENCES purchase (id),
    FOREIGN KEY (product) REFERENCES product (id)
);

CREATE TABLE review (
    id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
    user_id INTEGER,
    purchase_id INTEGER,
    product_id INTEGER,
    vote  INTEGER, CHECK (vote >= 0 AND vote <= 5),
    message VARCHAR(500),
    /*#buyer INTEGER,*/
    /*#FOREIGN KEY (buyer) REFERENCES purchase (id),*/
    FOREIGN KEY (user_id) REFERENCES app_user (id),
    FOREIGN KEY (purchase_id) REFERENCES purchase (id),
    FOREIGN KEY (product_id) REFERENCES product (id)
);
/* TODO Chat e proposte di prezzo? */

CREATE TABLE cart (
    id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
    user_id INTEGER NOT NULL UNIQUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    total_price DECIMAL(10, 2) DEFAULT 0.00,
    FOREIGN KEY (user_id) REFERENCES app_user (id)
);

CREATE TABLE product_in_cart (
    id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
    cart_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    FOREIGN KEY (cart_id) REFERENCES cart (id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product (id)
);