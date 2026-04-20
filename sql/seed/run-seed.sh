#!/bin/bash
# Wait for MySQL to be ready
for i in {1..30}; do
  docker exec huacai-mysql mysql -h127.0.0.1 -uroot -proot -e "SELECT 1" > /dev/null 2>&1
  if [ $? -eq 0 ]; then
    echo "MySQL is ready"
    break
  fi
  echo "Waiting for MySQL... ($i)"
  sleep 2
done

# Run the seed SQL
docker exec huacai-mysql mysql -h127.0.0.1 -uroot -proot huacai_system < "/Users/edy/Documents/tob /华彩系统1025/sql/seed/2026-04-19-demo-data.sql"
echo "Seed SQL executed with exit code: $?"

# Show results
echo "=== User count ==="
docker exec huacai-mysql mysql -h127.0.0.1 -uroot -proot huacai_system -e "SELECT COUNT(*) FROM sys_user;" 2>/dev/null

echo "=== Customer count ==="
docker exec huacai-mysql mysql -h127.0.0.1 -uroot -proot huacai_system -e "SELECT COUNT(*) FROM cust_customer;" 2>/dev/null
