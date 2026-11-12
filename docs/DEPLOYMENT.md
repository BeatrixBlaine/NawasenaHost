# Deployment

## Requirements
Docker with Compose v2, 4 GB RAM for one replica of everything (add ~400 MB per extra replica), ports 80 (and 443 with TLS).

## First start
```bash
git clone https://github.com/BeatrixBlaine/NawasenaHost.git && cd NawasenaHost
./scripts/generate-env.sh admin you@example.com
docker compose up -d --build
docker compose ps          # wait until everything is healthy, first start takes a few minutes
```
Open `http://<server>`.

1. Log in as the ADMIN from the script output.
2. **Hotels** → add the hotel (note its id).
3. **Rooms** → add rooms (or use batch).
4. **Inventory** → initialize the inventory for the coming period.
5. **Users** → create the MANAGER and STAFF accounts, assign the hotel.

## Upgrading an existing installation (older data)
Tables are updated by Hibernate (`ddl-auto=update`). After the first start of the new version, ADMIN calls once:
```
PUT  /api/customers/assign-hotel?hotelId=<id>      # customers created before multi hotel
POST /api/payments/backfill-hotel-id               # payments created before multi hotel
```
Users must log in again (token claims changed). Back up the databases before upgrading.

## Environment
See `.env.example`. Important values: `JWT_SECRET`, `JWT_RSA_*`, `MYSQL_ROOT_PASSWORD`, the five service client secrets (plain and BCrypt), `NAWASENAHOST_ADMIN_*`.

## Scaling
```bash
# in .env
BOOKING_SERVICE_REPLICAS=3
docker compose up -d
docker compose exec nginx nginx -s reload
```

## TLS
Terminate TLS in front of nginx (Caddy, Traefik, cloud load balancer) or mount a certificate in `deploy/nginx/nginx.conf`. Forwarded headers are already passed.

## Backup
```bash
docker compose exec mysql sh -c 'mysqldump -uroot -p"$MYSQL_ROOT_PASSWORD" --all-databases --single-transaction' > backup.sql
```
Restore: pipe the file into `mysql` the same way. Keep `.env` in a safe place, the RSA key and JWT secret are needed to keep issued tokens valid.

## Troubleshooting
- Service restarts again and again: `docker compose logs <service>`; usually a missing value in `.env`.
- 401 from services: auth-service is not healthy yet or `JWT_SECRET` differs between services.
- Panel shows login again and again: more than one panel replica without `ip_hash` (do not remove it).
