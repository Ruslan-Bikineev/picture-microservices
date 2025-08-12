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

INSERT INTO images (user_id, image_base64, is_deleted)
VALUES (1, 'image1', false),
       (2, 'image2', false),
       (3, 'image3', true),
       (4, 'image4', false),
       (5, 'image5', false),
       (5, 'image6', false);

INSERT INTO collection_images (collection_id, image_id)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (4, 4),
       (5, 5);

INSERT INTO comments (image_id, user_id, text, is_deleted)
VALUES (1, 1, 'Example comment', false),
       (2, 2, 'Example comment', false),
       (3, 3, 'Example comment', false),
       (4, 4, 'Example comment', false),
       (5, 5, 'Example comment', false),
       (5, 1, 'Example comment', true),
       (5, 2, 'Example comment', false),
       (5, 3, 'Example comment', true),
       (5, 4, 'Example comment', false);
