CREATE SEQUENCE user_seq;

CREATE TABLE "user"
(
    id               INTEGER DEFAULT NEXTVAL('user_seq') PRIMARY KEY,
    username         VARCHAR(20),
    code             VARCHAR(70),
    first_name       VARCHAR(50),
    last_name        VARCHAR(50),
    telephone_number VARCHAR(20),
    email            VARCHAR(90),
    address          VARCHAR(150),
    related_cart             INTEGER,
    FOREIGN KEY (related_cart) REFERENCES cart (id)
);

CREATE SEQUENCE product_seq;

CREATE TABLE product
(
    id          INTEGER DEFAULT NEXTVAL('product_seq') PRIMARY KEY,
    name        VARCHAR(50),
    bar_code    VARCHAR(70),
    description VARCHAR(500),
    price       FLOAT,
    quantity    FLOAT
);

CREATE SEQUENCE purchase_seq;

CREATE TABLE "purchase"
(
    id            INTEGER      DEFAULT NEXTVAL('purchase_seq') PRIMARY KEY,
    buyer         INTEGER REFERENCES "user" (id),
    purchase_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP

);

CREATE SEQUENCE product_in_purchase_seq;

CREATE TABLE product_in_purchase
(
    id               INTEGER DEFAULT NEXTVAL('product_in_purchase_seq') PRIMARY KEY,
    related_purchase INTEGER,
    price            FLOAT,
    product          INTEGER,
    quantity         INTEGER,
    related_cart     INTEGER,
    FOREIGN KEY (related_purchase) REFERENCES purchase (id),
    FOREIGN KEY (product) REFERENCES product (id),
    FOREIGN KEY (related_cart) REFERENCES cart (id)
);

CREATE SEQUENCE cart_seq;

CREATE TABLE cart
(
    id               INTEGER DEFAULT NEXTVAL('cart_seq') PRIMARY KEY
);