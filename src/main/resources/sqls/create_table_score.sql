CREATE TABLE score (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    student_id BIGINT,
    course_name VARCHAR(50),
    score INT
)