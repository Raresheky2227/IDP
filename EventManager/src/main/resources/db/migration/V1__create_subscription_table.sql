CREATE TABLE events (
                        id SERIAL PRIMARY KEY,
                        title VARCHAR(255),
                        description TEXT,
                        pdf_path VARCHAR(255)
);


CREATE TABLE subscriptions (
                               id SERIAL PRIMARY KEY,
                               user_id VARCHAR(255),
                               event_id VARCHAR(255),
                               UNIQUE(user_id, event_id)
);
