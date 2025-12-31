#!/bin/bash

# Database Backup Script for Learning Platform
# Run this script daily via cron: 0 2 * * * /path/to/backup.sh

# Configuration
BACKUP_DIR="/var/backups/learning-platform"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
RETENTION_DAYS=30

# Database credentials (use environment variables in production)
DB_USER="${POSTGRES_USER:-postgres}"
DB_PASSWORD="${POSTGRES_PASSWORD:-postgres}"

# Create backup directory if it doesn't exist
mkdir -p "$BACKUP_DIR"

echo "Starting backup at $(date)"

# Backup User Database
echo "Backing up userdb..."
PGPASSWORD="$DB_PASSWORD" pg_dump -h localhost -p 5433 -U "$DB_USER" userdb | gzip > "$BACKUP_DIR/userdb_$TIMESTAMP.sql.gz"
if [ $? -eq 0 ]; then
    echo "✓ userdb backup completed"
else
    echo "✗ userdb backup failed"
    exit 1
fi

# Backup Learning Database
echo "Backing up learningdb..."
PGPASSWORD="$DB_PASSWORD" pg_dump -h localhost -p 5434 -U "$DB_USER" learningdb | gzip > "$BACKUP_DIR/learningdb_$TIMESTAMP.sql.gz"
if [ $? -eq 0 ]; then
    echo "✓ learningdb backup completed"
else
    echo "✗ learningdb backup failed"
    exit 1
fi

# Delete old backups
echo "Cleaning up old backups (older than $RETENTION_DAYS days)..."
find "$BACKUP_DIR" -name "*.sql.gz" -type f -mtime +$RETENTION_DAYS -delete

echo "Backup completed successfully at $(date)"

# Optional: Upload to S3 (uncomment and configure)
# aws s3 cp "$BACKUP_DIR/userdb_$TIMESTAMP.sql.gz" s3://your-bucket/backups/
# aws s3 cp "$BACKUP_DIR/learningdb_$TIMESTAMP.sql.gz" s3://your-bucket/backups/
