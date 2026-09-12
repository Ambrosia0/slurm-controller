CREATE TABLE IF NOT EXISTS cluster(
    id bigserial PRIMARY KEY,
    hostname text not null unique,
    username text not null,
    password text,
    displayed_name text UNIQUE,
    http_schema text,
    ssh_port integer NOT NULL DEFAULT 22,
    daemon_port integer NOT NULL DEFAULT 6820,
    task_scheduler text
);

CREATE TABLE IF NOT EXISTS app_group(
    id bigserial PRIMARY KEY,
    group_name text unique not null,
    created_at timestamp default CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS app_user(
    id bigserial PRIMARY KEY,
    username text UNIQUE NOT NULL,
    password text,
    role text DEFAULT 'ROLE_USER',
    group_id bigint REFERENCES app_group(id) ON UPDATE CASCADE ON DELETE SET NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cluster_profile(
    cluster_id bigint REFERENCES cluster(id) ON DELETE CASCADE,
    user_id bigint REFERENCES app_user(id) ON DELETE CASCADE,
    profile_id integer,
    cluster_name text,
    password text,
    max_submit integer,
    max_tasks integer,
    max_tres text[],
    max_task_ttl integer,
    soft_limit bigint,
    hard_limit bigint,
    created_at timestamp default CURRENT_TIMESTAMP,
    PRIMARY KEY(cluster_id, user_id, cluster_name)
);