# OTA integration readiness

Booking from OTAs is **not implemented yet**, but nothing has to change in the core to add it.

## What is already in place
- `Booking.source` (`DIRECT` by default, e.g. `BOOKING_COM`, `AGODA`, `TRAVELOKA`) and `Booking.externalReference` (the OTA's reservation id).
- Unique `(source, externalReference)` → an OTA that retries or sends a webhook twice cannot create a double booking.
- Payment and report endpoints group by `source`, the dashboard already shows bookings and revenue per source.
- Service tokens (client credentials) with scopes: an OTA connector gets its own client, for example `booking.create`, `availability.read`, without a user account.
- Availability is a public, tenant checked API: `GET /availabilities/hotel/{id}/available-rooms`, block and unblock for closing sales.
- Cancel is idempotent and releases inventory, so cancellations from an OTA can call the same endpoint.
- Audit log records `source` in the details.

## How the connector would work
1. Add an `ota-client` in auth-service with the needed scopes.
2. A small adapter (new service or a job) maps the channel manager payload to `POST /api/customers/create-new-booking` with `source` and `externalReference`.
3. For availability and rates push, read `/inventory/hotel/{id}` and `/availabilities/hotel/{id}/blocks` and send them to the channel manager.
4. Handle `409` on a duplicate reference as "already imported".
