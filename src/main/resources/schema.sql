-- user
CREATE SEQUENCE user_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE "user"
(
    id           INT       DEFAULT NEXTVAL('user_seq') PRIMARY KEY,
    name         VARCHAR        NOT NULL,
    email        VARCHAR UNIQUE NOT NULL,
    phone_number VARCHAR UNIQUE NOT NULL,
    password     VARCHAR        NOT NULL,
    role         VARCHAR        NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_archived  BOOLEAN   DEFAULT FALSE
);

-- product
CREATE SEQUENCE product_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE "product"
(
    id          INT PRIMARY KEY,
    name        VARCHAR NOT NULL,
    description TEXT    NOT NULL,
    price       INT     NOT NULL,
    stock       INT     NOT NULL,
    status      VARCHAR NOT NULL,
    merchant_id INT     NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_archived BOOLEAN   DEFAULT FALSE
);

-- fk
ALTER TABLE product
    ADD CONSTRAINT fk_merchant_id FOREIGN KEY (merchant_id) REFERENCES "user" (id);