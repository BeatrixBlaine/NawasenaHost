# API reference

Base URL: `http://<host>/api` (through nginx and the gateway). Send `Authorization: Bearer <token>` from `POST /api/auth/login`.
Errors are JSON `{status, message, timeStamp}`. Status codes: 400 validation, 401 no token, 403 wrong role or hotel, 404 not found, 409 conflict (stale update, double booking, duplicate external reference).

Roles: **A** = ADMIN, **M** = MANAGER of that hotel, **S** = STAFF of that hotel. "Hotel" endpoints check that the `hotelId` belongs to the caller unless the caller is ADMIN.

## Auth and users (auth-service)
| Method | Path | Roles | Notes |
|---|---|---|---|
| POST | /auth/login | public | returns JWT |
| GET | /auth/users?hotelId= | A, M | M sees own hotel only |
| GET | /auth/users/{id} | A, M | |
| POST | /auth/users | A, M | M can only create STAFF in own hotel |
| PUT | /auth/users/{id} | A, M | `enabled` can disable an account |
| GET | /audit-logs?hotelId=&entityType=&actor=&page=&size= | A, M | M forced to own hotel |
| POST | /audit-logs | service token | `audit.write` scope |

## Hotels (hotel-service)
| Method | Path | Roles |
|---|---|---|
| GET | /hotels, /hotels/{id} | A (all), M/S (own) |
| POST, DELETE | /hotels, /hotels/{id} | A |
| PUT | /hotels/{id} | A, M (own) |

## Rooms (room-service)
| Method | Path | Roles |
|---|---|---|
| GET | /rooms/{hotelId}, /rooms/single-room/{roomId}, /rooms/total-rooms/{hotelId} | A, M, S |
| POST, PUT, DELETE | /rooms, /rooms/{id}, /rooms/batch | A, M |

## Availability and inventory (availability-service)
| Method | Path | Notes |
|---|---|---|
| GET | /availabilities/hotel/{hotelId}/available-rooms?checkIn=&checkOut= | free rooms |
| GET | /availabilities/check | one room, one range |
| POST | /availabilities/book | reserve nights (row lock, unique night) |
| DELETE | /availabilities/release | free the nights of a cancelled booking (idempotent) |
| POST | /availabilities/block | maintenance block, rejected when a night is booked |
| DELETE | /availabilities/unblock | |
| GET | /availabilities/hotel/{hotelId}/blocks | |
| POST | /inventory/initialize | create inventory rows |
| GET | /inventory/hotel/{hotelId}?from=&to= | total and reserved per day |
| GET | /inventory/hotel/{hotelId}/occupancy?from=&to= | daily rate and average |

## Bookings (booking-service)
| Method | Path | Notes |
|---|---|---|
| GET | /bookings/hotel/{hotelId}, /bookings/{id} | |
| GET | /bookings/hotel/{hotelId}/customer/{customerId} | |
| GET | /bookings/hotel/{hotelId}/report?from=&to= | totals, by status, by source, per day |
| POST | /bookings | accepts `source` and `externalReference` |
| PUT | /bookings/{id}/confirm-booking | |
| PUT | /bookings/{id}/cancel?reason= | PENDING (anyone) or CONFIRMED and paid (MANAGER/ADMIN, before check-out day ends): releases nights, refunds payment. COMPLETED or past stays: 409/400 |
| PUT | /bookings/{id}/complete | CONFIRMED → COMPLETED |
| DELETE | /bookings/cancel-pending-booking | used by payment-service |

## Customers (customer-service)
| Method | Path | Notes |
|---|---|---|
| GET | /customers/hotel/{hotelId}?q= | search name, email, phone |
| GET, PUT, DELETE | /customers/{id} | |
| POST | /customers/create-new-booking | customer and booking in one call |
| PUT | /customers/assign-hotel?hotelId= | A only, one time migration for old rows |

## Payments (payment-service)
| Method | Path | Notes |
|---|---|---|
| GET | /payments/hotel/{hotelId} | |
| GET | /payments/hotel/{hotelId}/report?from=&to= | revenue total, by method, by source, per day, refunds |
| POST | /payments/confirm-payment | PENDING → SUCCESS |
| PUT | /payments/refund-by-booking-id?bookingId= | idempotent |
| POST | /payments/backfill-hotel-id | A only, one time migration |

Swagger UI is available per service at `/my-ui.html` on the internal network.
