-- name: get-days
SELECT bed_time, wake_up_time
FROM days
ORDER BY bed_time DESC

--name: add-day!
INSERT
INTO days
VALUES
(:bed_time, :wake_up_time)

--name: update-day!
UPDATE
days
SET
bed_time = :bed_time
wake_up_time = :wake_up_time
WHERE bed_time = :bed_time

--name: clear-days!
DELETE
FROM days

--name: delete-day!
DELETE
FROM days
WHERE bed_time = :bed_time

