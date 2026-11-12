# Architecture

```
Browser ──► nginx ──► admin-panel-service (x N, ip_hash)
              │              │ WebClient
              └─ /api/ ──► api-gateway (x N) ──► Eureka lookup ──► services (x N)
                                                     auth · hotel · room · availability
                                                     booking · customer · payment
```

The panel talks to the same public API as any other client, so everything the panel can do is available to a future mobile app or channel manager.

## Data ownership

Each service owns its own MySQL database. No service reads another service's tables, only its REST API.

| Service | Entities |
|---|---|
| auth-service | `Auth` (users), **`AuditLog`** |
| hotel-service | `Hotel` |
| room-service | `Room` |
| availability-service | `Availability` (booked or blocked night), `RoomInventory` (total and reserved per room type per day) |
| booking-service | `Booking` (with `source`, `externalReference`, cancellation fields) |
| customer-service | `Customer` (with `hotelId`) |
| payment-service | `Payment` (with `hotelId`, `refundedAt`) |

No new services were added. Audit logs live in auth-service because it already is the identity owner, and every service can reach it with its service token.

## Booking flow

1. Panel or API calls `POST /api/customers/create-new-booking` (customer + booking data).
2. customer-service saves the customer and calls booking-service.
3. booking-service validates the room (room-service), checks and reserves the nights (availability-service), saves the booking as `PENDING` and asks payment-service to create the payment.
4. Confirm payment: payment-service confirms the booking first, then marks the payment `SUCCESS`.
5. Check-out: `PUT /api/bookings/{id}/complete` marks the booking `COMPLETED`.

## Cancel flow (also for confirmed and paid bookings)

`PUT /api/bookings/{id}/cancel?reason=...`  
Allowed for `PENDING` (any role) and `CONFIRMED` (MANAGER/ADMIN, before the check-out date). A `COMPLETED` stay cannot be cancelled.

1. The booking becomes `CANCELLED` with `cancelledAt` and `cancellationReason`.
2. availability-service releases the nights and decrements the inventory (`DELETE /api/availabilities/release`).
3. payment-service refunds: `SUCCESS` → `REFUNDED`, `PENDING` → `CANCELLED` (`PUT /api/payments/refund-by-booking-id`).
4. An audit entry is written.

Steps 2 and 3 are idempotent, so calling cancel again after a partial failure completes the work instead of failing.

## Audit trail

Every service publishes asynchronously through `AuditPublisher` to `POST /api/audit-logs` in auth-service. A failing audit call never fails the business call.
