INSERT INTO worker (name, phone, status)
VALUES ('演示工人01', '13800000003', 'ENABLED');

UPDATE app_user
SET worker_id = (
    SELECT id
    FROM worker
    WHERE name = '演示工人01'
    ORDER BY id DESC
    LIMIT 1
)
WHERE username = 'worker01';
