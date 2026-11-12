#!/usr/bin/env bash
# Creates .env with fresh secrets for a new NawasenaHost deployment.
# Needs: openssl, and either htpasswd (apache2-utils) or docker for the bcrypt hashes.
# Usage: ./scripts/generate-env.sh [admin-username] [admin-email]

set -euo pipefail

cd "$(dirname "$0")/.."

if [ -f .env ]; then
  echo ".env already exists, remove it first if you really want new secrets"
  exit 1
fi

ADMIN_USERNAME="${1:-admin}"
ADMIN_EMAIL="${2:-admin@nawasenahost.local}"

rand() { openssl rand -base64 24 | tr -d '/+=\n' | cut -c1-28; }

bcrypt() {
  if command -v htpasswd >/dev/null 2>&1; then
    htpasswd -nbBC 10 "" "$1" | cut -d: -f2 | tr -d '\n' | sed 's/^\$2y/$2a/'
  else
    docker run --rm httpd:2.4-alpine htpasswd -nbBC 10 "" "$1" | cut -d: -f2 | tr -d '\n\r' | sed 's/^\$2y/$2a/'
  fi
}

# user token signing key, Base64 as the services expect it
JWT_SECRET="$(openssl rand -base64 48 | tr -d '\n')"

# service token signing key, shared by every auth-service replica (PKCS8 / X509 in DER, Base64)
openssl genrsa -out /tmp/nh_rsa.pem 2048 2>/dev/null
JWT_RSA_PRIVATE_KEY="$(openssl pkcs8 -topk8 -nocrypt -in /tmp/nh_rsa.pem -outform DER | openssl base64 -A)"
JWT_RSA_PUBLIC_KEY="$(openssl rsa -in /tmp/nh_rsa.pem -pubout -outform DER 2>/dev/null | openssl base64 -A)"
rm -f /tmp/nh_rsa.pem

MYSQL_ROOT_PASSWORD="$(rand)"
ADMIN_PASSWORD="$(rand)"
MANAGER_PASSWORD="$(rand)"

BOOKING_SECRET="$(rand)"; PAYMENT_SECRET="$(rand)"; AVAILABILITY_SECRET="$(rand)"
ROOM_SECRET="$(rand)"; CUSTOMER_SECRET="$(rand)"

cat > .env <<ENV
TAG=latest
HTTP_PORT=80

MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD}

JWT_SECRET=${JWT_SECRET}
JWT_RSA_PRIVATE_KEY=${JWT_RSA_PRIVATE_KEY}
JWT_RSA_PUBLIC_KEY=${JWT_RSA_PUBLIC_KEY}

NAWASENAHOST_ADMIN_USERNAME=${ADMIN_USERNAME}
NAWASENAHOST_ADMIN_PASSWORD=${ADMIN_PASSWORD}
NAWASENAHOST_ADMIN_EMAIL=${ADMIN_EMAIL}

NAWASENAHOST_MANAGER_USERNAME=manager
NAWASENAHOST_MANAGER_PASSWORD=${MANAGER_PASSWORD}
NAWASENAHOST_MANAGER_EMAIL=manager@nawasenahost.local
NAWASENAHOST_MANAGER_HOTEL_ID=1

SERVICE_CLIENT_SECRET=${BOOKING_SECRET}
SERVICE_CLIENT_SECRET_BCRYPT=$(bcrypt "$BOOKING_SECRET")
PAYMENT_CLIENT_SECRET=${PAYMENT_SECRET}
PAYMENT_CLIENT_SECRET_BCRYPT=$(bcrypt "$PAYMENT_SECRET")
AVAILABILITY_CLIENT_SECRET=${AVAILABILITY_SECRET}
AVAILABILITY_CLIENT_SECRET_BCRYPT=$(bcrypt "$AVAILABILITY_SECRET")
ROOM_CLIENT_SECRET=${ROOM_SECRET}
ROOM_CLIENT_SECRET_BCRYPT=$(bcrypt "$ROOM_SECRET")
CUSTOMER_CLIENT_SECRET=${CUSTOMER_SECRET}
CUSTOMER_CLIENT_SECRET_BCRYPT=$(bcrypt "$CUSTOMER_SECRET")

# replicas per service, 1 is enough for a single server
API_GATEWAY_REPLICAS=2
ADMIN_PANEL_SERVICE_REPLICAS=2
AUTH_SERVICE_REPLICAS=2
HOTEL_SERVICE_REPLICAS=1
ROOM_SERVICE_REPLICAS=2
AVAILABILITY_SERVICE_REPLICAS=2
BOOKING_SERVICE_REPLICAS=2
CUSTOMER_SERVICE_REPLICAS=2
PAYMENT_SERVICE_REPLICAS=2
ENV

chmod 600 .env
echo ".env created. First ADMIN login: ${ADMIN_USERNAME} / ${ADMIN_PASSWORD}"
echo "Change that password after the first login."
