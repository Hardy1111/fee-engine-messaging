CREATE TABLE user_message(

    message_id UUID PRIMARY KEY,
    name VARCHAR(45) NOT NULL,
    email VARCHAR(255) NOT NULL,
    sent_at TIMESTAMP NOT NULL
)