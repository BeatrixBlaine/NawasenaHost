# Security

## Tokens
- **User token** – HS256, signed with `JWT_SECRET`, claims `sub`, `role`, `hotelId`, 1 hour.
- **Service token** – RS256, issued by auth-service (client credentials), scopes such as `room.read`, `booking.create`, `payment.read`, `audit.write`. Verified through the JWKS endpoint. The key pair comes from `JWT_RSA_PRIVATE_KEY` / `JWT_RSA_PUBLIC_KEY` so all auth replicas sign and verify with the same key.
- Every service accepts both. A token without a `role` claim is a service call.

## Roles
| Role | Scope |
|---|---|
| ADMIN | Whole system: all hotels, hotel management, all users, all audit logs |
| MANAGER | The hotel on the account: rooms, availability, bookings, payments, customers, STAFF accounts, audit logs of the hotel |
| STAFF | Front desk of the hotel: bookings, customers, availability. No room, user or audit access |

`hotelId` is read from the token, never from the request. `TenantGuard` in every service compares it with the hotel of the data and answers 403 on a mismatch.

## Accounts
- Passwords are BCrypt hashes. A user cannot delete or disable their own account.
- The first ADMIN is created from `NAWASENAHOST_ADMIN_*` on startup. Change that password after the first login.

## Panel
Session based login, the token is kept server side, CSRF protection on every form, security headers, Chart.js is pinned to an exact version.

## Audit log
Fields: time, actor, role, hotelId, action, entityType, entityId, details. Actions include LOGIN, USER_*, ROOM_*, HOTEL_*, BOOKING_CREATED/CONFIRMED/CANCELLED/COMPLETED, PAYMENT_CONFIRMED/REFUNDED, BLOCK/UNBLOCK. Logs are append only, there is no update or delete endpoint.

## Production checklist
- Put TLS in front of nginx (certificate on the host or a TLS terminating proxy) and keep only nginx published.
- Keep `.env` out of Git, `chmod 600`.
- Rotate service client secrets by changing both values in `.env` and restarting.
