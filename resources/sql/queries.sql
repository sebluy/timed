-- name: get-bed-times
SELECT *
FROM bedtimes
ORDER BY time DESC

--name: insert-bed-time!
INSERT
INTO bedtimes
(time)
VALUES (:time)

--name: get-bed-time
SELECT *
FROM bedtimes
WHERE time = :time

--name: clear-bed-times!
DELETE
FROM bedtimes

--name: delete-bed-time-sql!
DELETE
FROM bedtimes
WHERE time = :time
