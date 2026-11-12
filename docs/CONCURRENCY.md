# Concurrency control and load balancing

| Risk | Control |
|---|---|
| Two guests book the same room and night | `Availability` has a unique key `(roomId, date)`. Booking uses a pessimistic write lock on the inventory row, the loser gets **409**. |
| Inventory counters drift | `RoomInventory` has `@Version` and a pessimistic lock when reserving and releasing. |
| Two staff change the same booking, room, hotel or payment | `@Version` optimistic locking. A stale update answers **409** "changed by someone else, reload". |
| A payment is confirmed or refunded twice | `@Version` on `Payment`, unique `bookingId`, status checks before every change. |
| The same OTA booking is delivered twice | Unique `(source, externalReference)` on `Booking`, the duplicate answers 409. |
| A cross service call fails halfway | Release and refund are idempotent, repeating `cancel` finishes the job. A failed booking creation removes the new customer again. |
| Retried requests | Cancel, release, refund and unblock can be repeated safely. |

## Load balancing
- **Edge**: nginx. `/api/` is round robin over the api-gateway replicas with passive health checks (`max_fails`, `fail_timeout`) and retry on 502/503. `/` goes to the panel with `ip_hash`, because the panel keeps the login session in memory.
- **Between services**: the gateway uses `lb://service-name`, Feign uses Eureka, both balance over every registered instance (Spring Cloud LoadBalancer, round robin).
- **Scaling**: set `*_REPLICAS` in `.env` and run `docker compose up -d`, then `docker compose exec nginx nginx -s reload` so nginx sees the new gateway and panel replicas.
- **Stateless**: all services are stateless except the panel session (sticky). If a panel replica dies, its users log in again.
- Auth replicas must share the RSA key, which `generate-env.sh` takes care of.
