-- name: get-activities
SELECT DISTINCT activity
FROM sessions

--name: delete-activity!
DELETE
FROM sessions
WHERE activity = :activity

-- name: get-sessions
SELECT start, finish
FROM sessions
WHERE activity = :activity
ORDER BY start DESC

--name: add-session!
INSERT
INTO sessions
VALUES
(:activity, :start, :finish)

--name: update-session!
UPDATE
sessions
SET
start = :start,
finish = :finish
WHERE start = :start

--name: clear-sessions!
DELETE
FROM sessions

--name: delete-session!
DELETE
FROM sessions
WHERE start = :start

