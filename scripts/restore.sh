#!/bin/bash

# Database Restore Script
# Restores the 'learning_db' from a backup file

if [ -z "$1" ]; then
    echo "Usage: ./restore.sh <backup_file.sql.gz>"
    exit 1
fi

BACKUP_FILE="$1"
DB_CONTAINER="learning-platform-db"
DB_NAME="learning_db"
DB_USER="postgres"

if [ ! -f "$BACKUP_FILE" ]; then
    echo "❌ Backup file not found: $BACKUP_FILE"
    exit 1
fi

echo "⚠️  WARNING: This will OVERWRITE the '$DB_NAME' database!"
read -p "Are you sure? (type 'yes' to confirm): " confirm

if [ "$confirm" != "yes" ]; then
    echo "Restore cancelled."
    exit 0
fi

echo "[$(date)] Starting restore..."

# Drop and recreate database to ensure clean state
echo "Recreating database..."
docker exec -i "$DB_CONTAINER" psql -U "$DB_USER" -c "DROP DATABASE IF EXISTS $DB_NAME;"
docker exec -i "$DB_CONTAINER" psql -U "$DB_USER" -c "CREATE DATABASE $DB_NAME;"

# Restore
echo "Restoring data..."
gunzip -c "$BACKUP_FILE" | docker exec -i "$DB_CONTAINER" psql -U "$DB_USER" -d "$DB_NAME"

if [ $? -eq 0 ]; then
    echo "✓ Restore successful"
else
    echo "❌ Restore failed"
    exit 1
fi

echo "[$(date)] Restore complete."
