CREATE TABLE IF NOT EXISTS collections
(
    id BIGSERIAL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS users
(
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(255) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    collection_id BIGINT UNIQUE,
    CONSTRAINT fk_user_collection
        FOREIGN KEY (collection_id)
            REFERENCES collections (id)
            ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS images
(
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT NOT NULL,
    image_base64 text   NOT NULL,
    is_deleted   BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_image_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS collection_images
(
    collection_id BIGINT NOT NULL,
    image_id      BIGINT NOT NULL,
    PRIMARY KEY (collection_id, image_id),
    CONSTRAINT fk_collection
        FOREIGN KEY (collection_id)
            REFERENCES collections (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_collection_image
        FOREIGN KEY (image_id)
            REFERENCES images (id)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments
(
    id         BIGSERIAL PRIMARY KEY,
    image_id   BIGINT       NOT NULL,
    user_id    BIGINT       NOT NULL,
    text       VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_comment_image
        FOREIGN KEY (image_id)
            REFERENCES images (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_comment_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);
