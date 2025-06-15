#!/bin/bash

# EC2 Disk Space Cleanup Script
# This script cleans up common space-consuming items on EC2

echo "=== EC2 Disk Space Cleanup ==="
echo "Current disk usage:"
df -h /

echo -e "\n=== Step 1: Safe Docker cleanup (preserving running containers) ==="
# Only remove stopped containers, not running ones
docker container prune -f

# Remove dangling images (untagged images)
docker image prune -f

# Remove unused networks (not attached to running containers)
docker network prune -f

# Remove unused build cache
docker builder prune -af

echo -e "\n=== Step 2: APT package cleanup ==="
# Clean APT cache
sudo apt-get autoremove -y
sudo apt-get autoclean
sudo apt-get clean

echo -e "\n=== Step 3: Log cleanup ==="
# Clean systemd journal logs (keep only last 3 days)
sudo journalctl --vacuum-time=3d

# Clean older log files
sudo find /var/log -type f -name "*.log" -mtime +7 -delete 2>/dev/null || true
sudo find /var/log -type f -name "*.gz" -mtime +7 -delete 2>/dev/null || true

echo -e "\n=== Step 4: Temporary files cleanup ==="
# Clean /tmp directory
sudo find /tmp -type f -atime +7 -delete 2>/dev/null || true

# Clean user cache
rm -rf ~/.cache/* 2>/dev/null || true

echo -e "\n=== Step 5: Other cleanup ==="
# Clean snap cache if snap is installed
if command -v snap &> /dev/null; then
    sudo snap set system refresh.retain=2
    LANG=en_US.UTF-8 sudo snap list --all | awk '/disabled/{print $1, $3}' | while read snapname revision; do
        sudo snap remove "$snapname" --revision="$revision" 2>/dev/null || true
    done
fi

echo -e "\n=== Cleanup Complete ==="
echo "Disk usage after cleanup:"
df -h /

echo -e "\nRunning containers (preserved):"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

echo -e "\nLargest directories in /home:"
du -h --max-depth=1 /home/ 2>/dev/null | sort -hr | head -10

echo -e "\nLargest files in /var:"
sudo find /var -type f -size +100M -exec ls -lh {} \; 2>/dev/null | head -10

echo -e "\n=== Cleanup script finished ==="
echo "Note: All running containers have been preserved."
