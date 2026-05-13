ALTER TABLE worker
    ADD COLUMN team_id BIGINT NULL;

UPDATE worker
SET team_id = (
    SELECT id
    FROM team
    WHERE name = '钢筋班组'
    ORDER BY id
    LIMIT 1
)
WHERE name IN ('张三', '演示工人01')
  AND team_id IS NULL;

UPDATE worker
SET team_id = (
    SELECT id
    FROM team
    WHERE name = '木工班组'
    ORDER BY id
    LIMIT 1
)
WHERE name IN ('李四')
  AND team_id IS NULL;

ALTER TABLE worker
    ADD CONSTRAINT fk_worker_team FOREIGN KEY (team_id) REFERENCES team (id);
