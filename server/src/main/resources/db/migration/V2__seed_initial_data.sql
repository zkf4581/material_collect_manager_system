INSERT INTO app_user (username, password, role_code, status, project_id)
VALUES ('admin', '{noop}123456', 'ADMIN', 'ENABLED', 1),
       ('keeper', '{noop}123456', 'KEEPER', 'ENABLED', 1),
       ('worker01', '{noop}123456', 'WORKER', 'ENABLED', 1);

INSERT INTO project (name, location, status)
VALUES ('示例项目A', '杭州', 'ENABLED');

INSERT INTO team (project_id, name, status)
VALUES (1, '钢筋班组', 'ENABLED'),
       (1, '木工班组', 'ENABLED');

INSERT INTO worker (name, phone, status)
VALUES ('张三', '13800000001', 'ENABLED'),
       ('李四', '13800000002', 'ENABLED');

INSERT INTO material_item (name, unit_code, status)
VALUES ('钢筋余料', 'KG', 'ENABLED'),
       ('木方', 'GEN', 'ENABLED'),
       ('模板', 'ZHANG', 'ENABLED');

INSERT INTO point_rule (material_item_id, unit_code, base_point, condition_code, condition_factor, status)
VALUES (1, 'KG', 1.00, 'OK', 1.00, 'ENABLED'),
       (2, 'GEN', 2.00, 'OK', 1.00, 'ENABLED'),
       (3, 'ZHANG', 5.00, 'MINOR', 0.80, 'ENABLED');

INSERT INTO reward_item (name, points_cost, stock, status)
VALUES ('毛巾', 50, 100, 'ENABLED'),
       ('洗衣液', 80, 60, 'ENABLED'),
       ('手套', 30, 200, 'ENABLED');
