INSERT INTO project (id, name, location, status)
VALUES (1, '示例项目A', '杭州', 'ENABLED');

INSERT INTO team (id, project_id, name, status)
VALUES (11, 1, '钢筋班组', 'ENABLED'),
       (12, 1, '木工班组', 'ENABLED');

INSERT INTO worker (id, name, phone, status)
VALUES (21, '张三', '13800000001', 'ENABLED'),
       (22, '李四', '13800000002', 'ENABLED'),
       (23, '演示工人01', '13800000003', 'ENABLED');

INSERT INTO app_user (id, username, password, role_code, status, project_id, worker_id)
VALUES (1, 'admin', '{noop}123456', 'ADMIN', 'ENABLED', 1, NULL),
       (2, 'keeper', '{noop}123456', 'KEEPER', 'ENABLED', 1, NULL),
       (3, 'worker01', '{noop}123456', 'WORKER', 'ENABLED', 1, 23);

INSERT INTO material_item (id, name, unit_code, status)
VALUES (31, '钢筋余料', 'KG', 'ENABLED'),
       (32, '木方', 'GEN', 'ENABLED'),
       (33, '模板', 'ZHANG', 'ENABLED');

INSERT INTO point_rule (id, material_item_id, unit_code, base_point, condition_code, condition_factor, status)
VALUES (41, 31, 'KG', 1.00, 'OK', 1.00, 'ENABLED'),
       (42, 32, 'GEN', 2.00, 'OK', 1.00, 'ENABLED'),
       (43, 33, 'ZHANG', 5.00, 'MINOR', 0.80, 'ENABLED');

INSERT INTO reward_item (id, name, points_cost, stock, status)
VALUES (51, '毛巾', 50, 100, 'ENABLED'),
       (52, '洗衣液', 80, 60, 'ENABLED'),
       (53, '手套', 30, 200, 'ENABLED');

INSERT INTO point_account (id, project_id, worker_id, balance)
VALUES (61, 1, 23, 120);
