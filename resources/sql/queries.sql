-- name: get-bed-times
SELECT *
FROM bedtimes

--name: insert-bed-time!
INSERT
INTO bedtimes
(time)
VALUES (:time)

--name: get-bed-time
SELECT *
FROM bedtimes
WHERE date(time) = :date

--name: clear-bed-times!
DELETE
FROM bedtimes

