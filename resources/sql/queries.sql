-- name: get-days
SELECT *
FROM days
ORDER BY date DESC

--name: add-new-day!
INSERT
INTO days
VALUES
(:date, :wake_up_time)

--name: add-bed-time!
UPDATE
days
SET bed_time = :bed_time
WHERE date = :date

--name: clear-days!
DELETE
FROM days

--name: delete-day!
DELETE
FROM days
WHERE date = :date

