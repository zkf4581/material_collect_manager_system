USE material_points;

INSERT INTO project (name, location, status)
VALUES ('示例项目A', '杭州', 'ENABLED')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO worker (name, phone, status)
VALUES ('张三', '13800000001', 'ENABLED'),
       ('李四', '13800000002', 'ENABLED')
ON DUPLICATE KEY UPDATE name = VALUES(name);
