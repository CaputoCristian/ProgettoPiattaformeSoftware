CREATE TABLE orders.product
(
    id            INT AUTO_INCREMENT NOT NULL,
    name          VARCHAR(50)        NULL,
    brand         VARCHAR(50)        NULL,
    product_type  VARCHAR(50)        NULL,
    `description` VARCHAR(500)       NULL,
    price         FLOAT              NULL,
    quantity      INT                NULL,
    CONSTRAINT pk_product PRIMARY KEY (id)
);

CREATE TABLE orders.product_in_purchase
(
    id       INT AUTO_INCREMENT NOT NULL,
    product  VARCHAR(90)        NULL,
    quantity VARCHAR(90)        NULL,
    CONSTRAINT pk_product_in_purchase PRIMARY KEY (id)
);

CREATE TABLE orders.purchase
(
    id            INT AUTO_INCREMENT NOT NULL,
    buyer         INT                NULL,
    purchase_time datetime(6)        NULL,
    CONSTRAINT pk_purchase PRIMARY KEY (id)
);

CREATE TABLE orders.review
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    vote        INT                   NOT NULL,
    message     VARCHAR(255)          NOT NULL,
    user_id     INT                   NULL,
    purchase_id INT                   NULL,
    product_id  INT                   NULL,
    CONSTRAINT pk_review PRIMARY KEY (id)
);

CREATE TABLE orders.shop
(
    id      INT AUTO_INCREMENT NOT NULL,
    user_id INT                NULL,
    CONSTRAINT pk_shop PRIMARY KEY (id)
);

CREATE TABLE orders.user
(
    id               INT AUTO_INCREMENT NOT NULL,
    cf               VARCHAR(16)        NULL,
    first_name       VARCHAR(50)        NULL,
    last_name        VARCHAR(50)        NULL,
    telephone_number VARCHAR(20)        NULL,
    email            VARCHAR(90)        NULL,
    address          VARCHAR(150)       NULL,
    birth_date       datetime           NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

ALTER TABLE orders.shop
    ADD CONSTRAINT uc_shop_user UNIQUE (user_id);

ALTER TABLE orders.purchase
    ADD CONSTRAINT FK_PURCHASE_ON_BUYER FOREIGN KEY (buyer) REFERENCES orders.user (id);

ALTER TABLE orders.review
    ADD CONSTRAINT FK_REVIEW_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES orders.product (id);

ALTER TABLE orders.review
    ADD CONSTRAINT FK_REVIEW_ON_PURCHASE FOREIGN KEY (purchase_id) REFERENCES orders.purchase (id);

ALTER TABLE orders.review
    ADD CONSTRAINT FK_REVIEW_ON_USER FOREIGN KEY (user_id) REFERENCES orders.user (id);

ALTER TABLE orders.shop
    ADD CONSTRAINT FK_SHOP_ON_USER FOREIGN KEY (user_id) REFERENCES orders.user (id);