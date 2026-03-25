#!/usr/bin/env bash
set -euo pipefail

SERVICE_NAME="esp32-host-app"
INSTALL_DIR="/opt/app"
SERVICE_PATH="/etc/systemd/system/${SERVICE_NAME}.service"

if [[ "${EUID}" -ne 0 ]]; then
    echo "请使用 root 执行卸载脚本。"
    exit 1
fi

if systemctl list-unit-files "${SERVICE_NAME}.service" >/dev/null 2>&1; then
    systemctl disable --now "${SERVICE_NAME}.service" || true
fi

rm -f "${SERVICE_PATH}"
systemctl daemon-reload
systemctl reset-failed

rm -rf "${INSTALL_DIR}"

echo
echo "卸载完成。"
echo "如需删除系统用户和组，请手动执行："
echo "  userdel esp32-host-app"
echo "  groupdel esp32-host-app"
