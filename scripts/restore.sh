#!/bin/bash

# Database Restore Script for Learning Platform
# Usage: ./restore.sh <backup_file.sql.gz> <database_name>

if [ $# -lt 2 ]; then
    echo "Usage: $0 <backup_file.sql.gz> <database_name>"
    echo "Example: $0 /var/backups/learning-platform/userdb_20250101.sql.gz userdb"
    exit 1
fi

BACKUP_FILE=$1
DATABASE=$2
DB_USER="${POSTGRES_USER:-postgres}"
DB_PASSWORD="${POSTGRES_PASSWORD:-postgres}"

# Determine port based on database name
if [ "$DATABASE" == "userdb" ]; then
    DB_PORT=5433
elif [ "$DATABASE" == "learningdb" ]; then
    DB_PORT=5434
else
    echo "Unknown database: $DATABASE"
    exit 1
fi

echo "⚠️  WARNING: This will DROP and recreate the database: $DATABASE"
read -p "Are you sure you want to continue? (yes/no): " confirm

if [ "$confirm" != "yes" ]; then
    echo "Restore cancelled"
    exit 0
fi

echo "Starting restore of $DATABASE from $BACKUP_FILE..."

# Drop existing database
echo "Dropping existing database..."
PGPASSWORD="$DB_PASSWORD" psql -h localhost -p "$DB_PORT" -U "$DB_USER" -c "DROP DATABASE IF EXISTS $DATABASE;"

# Create fresh database
echo "Creating fresh database..."
PGPASSWORD="$DB_PASSWORD" psql -h localhost -p "$DB_PORT" -U "$DB_USER" -c "CREATE DATABASE $DATABASE;"

# Restore from backup
echo "Restoring from backup..."
gunzip -c "$BACKUP_FILE" | PGPASSWORD="$DB_PASSWORD" psql -h localhost -p "$DB_PORT" -U "$DB_USER" "$DATABASE"

if [ $? -eq 0 ]; then
    echo "✓ Restore completed successfully"
else
    echo "✗ Restore failed"
    exit 1
fi

echo "Database $DATABASE restored from $BACKUP_FILE"
