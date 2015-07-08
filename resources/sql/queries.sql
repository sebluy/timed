-- name: get-activities
SELECT *
FROM sessions

--name: delete-activity!
DELETE
FROM sessions
WHERE activity = :activity

--name: add-session!
INSERT
INTO sessions
VALUES
(:activity, :start, :finish)

--name: delete-session!
DELETE
FROM sessions
WHERE start = :start

--name: clear-sessions!
DELETE
FROM sessions


