CREATE TABLE IF NOT EXISTS removed_members (
    discord_id TEXT PRIMARY KEY,
    mojang_id TEXT NOT NULL,
    login_count INTEGER NOT NULL,
    join_date TEXT NOT NULL,
    leave_date TEXT
);
