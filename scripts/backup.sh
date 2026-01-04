#!/bin/bash

# Database Backup Script
# Backs up the single shared 'learning_db' from the postgres container

# Configuration
BACKUP_DIR="/var/backups/learning-platform"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
RETENTION_DAYS=30
DB_CONTAINER="learning-platform-db"
DB_NAME="learning_db"
DB_USER="postgres"

# Ensure backup directory exists
mkdir -p "$BACKUP_DIR"

echo "[$(date)] Starting backup..."

# Check if container is running
if ! docker ps | grep -q "$DB_CONTAINER"; then
    echo "❌ Database container '$DB_CONTAINER' is not running!"
    exit 1
fi

# Perform backup
# We execute pg_dump inside the container to avoid needing postgres tools on host
docker exec -t "$DB_CONTAINER" pg_dump -U "$DB_USER" "$DB_NAME" | gzip > "$BACKUP_DIR/${DB_NAME}_$TIMESTAMP.sql.gz"

if [ ${PIPESTATUS[0]} -eq 0 ]; then
    echo "✓ Backup successful: $BACKUP_DIR/${DB_NAME}_$TIMESTAMP.sql.gz"
else
    echo "❌ Backup failed!"
    rm -f "$BACKUP_DIR/${DB_NAME}_$TIMESTAMP.sql.gz"
    exit 1
fi

# Cleanup old backups
find "$BACKUP_DIR" -name "${DB_NAME}_*.sql.gz" -mtime +$RETENTION_DAYS -delete
echo "✓ Cleaned up backups older than $RETENTION_DAYS days"

echo "[$(date)] Backup complete."
