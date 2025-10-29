INSERT INTO collections DEFAULT
VALUES;
INSERT INTO collections DEFAULT
VALUES;
INSERT INTO collections DEFAULT
VALUES;
INSERT INTO collections DEFAULT
VALUES;
INSERT INTO collections DEFAULT
VALUES;

INSERT INTO users (username, password, collection_id)
VALUES ('User1', 'Pass1', 1),
       ('User2', 'Pass2', 2),
       ('User3', 'Pass3', 3),
       ('User4', 'Pass4', 4),
       ('User5', 'Pass5', 5);

INSERT INTO collection_images (collection_id, image_id)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (4, 4),
       (5, 5);
