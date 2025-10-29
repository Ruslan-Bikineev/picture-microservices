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
