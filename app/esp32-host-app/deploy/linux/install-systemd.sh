#!/usr/bin/env bash
set -euo pipefail

SERVICE_NAME="esp32-host-app"
INSTALL_DIR="/opt/app"
SERVICE_PATH="/etc/systemd/system/${SERVICE_NAME}.service"
APP_USER="esp32-host-app"
APP_GROUP="esp32-host-app"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
PUBLISH_DIR="$(mktemp -d)"

cleanup() {
    rm -rf "${PUBLISH_DIR}"
}

trap cleanup EXIT

if [[ "${EUID}" -ne 0 ]]; then
    echo "请使用 root 执行安装脚本。"
    exit 1
fi

if ! command -v dotnet >/dev/null 2>&1; then
    echo "未找到 dotnet，请先安装 .NET 8 runtime 和 SDK。"
    exit 1
fi

echo "发布应用到临时目录..."
dotnet publish "${REPO_ROOT}/esp32-host-app.csproj" -c Release -o "${PUBLISH_DIR}"

if ! getent group "${APP_GROUP}" >/dev/null 2>&1; then
    echo "创建系统组 ${APP_GROUP}..."
    groupadd --system "${APP_GROUP}"
fi

if ! id -u "${APP_USER}" >/dev/null 2>&1; then
    echo "创建系统用户 ${APP_USER}..."
    useradd \
        --system \
        --gid "${APP_GROUP}" \
        --home-dir /nonexistent \
        --shell /usr/bin/nologin \
        --no-create-home \
        "${APP_USER}"
fi

echo "安装应用到 ${INSTALL_DIR}..."
install -d -m 0755 "${INSTALL_DIR}"
find "${INSTALL_DIR}" -mindepth 1 -maxdepth 1 -exec rm -rf {} +
cp -a "${PUBLISH_DIR}/." "${INSTALL_DIR}/"
chown -R "${APP_USER}:${APP_GROUP}" "${INSTALL_DIR}"

echo "安装 systemd unit..."
install -D -m 0644 "${SCRIPT_DIR}/${SERVICE_NAME}.service" "${SERVICE_PATH}"

echo "重新加载并启动服务..."
systemctl daemon-reload
systemctl enable --now "${SERVICE_NAME}.service"

echo
echo "安装完成。"
echo "查看状态: systemctl status ${SERVICE_NAME}.service"
echo "查看日志: journalctl -u ${SERVICE_NAME}.service -f"
