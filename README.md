# Bank Service

Spring Boot 3.3 / Java 21 REST service for managing banks, their branch addresses, and customer accounts.

## Domain model

```
Account  *──1  Bank  1──1  Address
```

| Entity  | Fields |
|---------|--------|
| Account | accountNumber (generated, unique), accountHolderName, balance, accountType, bank |
| Bank    | bankName, ifscCode (unique), branchName, address |
| Address | street, city, state, pinCode |

- `Account → Bank` is `@ManyToOne` (a bank branch has many accounts).
- `Bank → Address` is `@OneToOne` with cascade + orphan removal (an address is owned by its bank).
- `AccountType`: `SAVINGS`, `CURRENT`, `SALARY`, `FIXED_DEPOSIT`.

## Running

Local (in-memory H2, no setup needed):

```bash
mvn spring-boot:run
```

With PostgreSQL via Docker:

```bash
docker compose up --build
```

Swagger UI: http://localhost:8080/swagger-ui.html · Health: http://localhost:8080/actuator/health

### Configuration (env vars)

| Variable | Default | Notes |
|----------|---------|-------|
| `SPRING_PROFILES_ACTIVE` | – | `prod` for PostgreSQL |
| `DB_URL` | H2 in-memory | e.g. `jdbc:postgresql://host:5432/bankdb` |
| `DB_USERNAME` / `DB_PASSWORD` | `sa` / empty | required in `prod` |
| `DB_POOL_SIZE` | 10 | prod only |
| `API_DOCS_ENABLED` | false in prod | enables Swagger in prod |
| `SERVER_PORT` | 8080 | |

## API (`/api/v1`)

| Method | Path | Description |
|--------|------|-------------|
| POST | `/banks` | Create bank with address |
| GET | `/banks` | List banks (`page`, `size`, `sort`) |
| GET | `/banks/{id}` | Get bank |
| GET | `/banks/ifsc/{ifscCode}` | Get bank by IFSC |
| PUT | `/banks/{id}` | Update bank and address |
| DELETE | `/banks/{id}` | Delete bank (409 if it has accounts) |
| POST | `/accounts` | Open account |
| GET | `/accounts?bankId=` | List accounts, optional bank filter |
| GET | `/accounts/{accountNumber}` | Get account |
| PUT | `/accounts/{accountNumber}` | Update holder name / type |
| POST | `/accounts/{accountNumber}/deposit` | Deposit `{ "amount": 100.00 }` |
| POST | `/accounts/{accountNumber}/withdraw` | Withdraw `{ "amount": 50.00 }` |
| DELETE | `/accounts/{accountNumber}` | Close account |

Example:

```bash
curl -X POST localhost:8080/api/v1/banks -H "Content-Type: application/json" -d '{
  "bankName": "State Bank of India", "ifscCode": "SBIN0001234", "branchName": "MG Road",
  "address": {"street": "1 MG Road", "city": "Bengaluru", "state": "Karnataka", "pinCode": "560001"}
}'

curl -X POST localhost:8080/api/v1/accounts -H "Content-Type: application/json" -d '{
  "accountHolderName": "Asha Rao", "openingBalance": 1000.00, "accountType": "SAVINGS", "bankId": 1
}'
```

Errors are returned as RFC 7807 `ProblemDetail` JSON; validation errors include a per-field `errors` map.

## Design notes

- **Money**: `BigDecimal` / `NUMERIC(19,2)`; amounts with more than 2 decimals are rejected; a DB check constraint forbids negative balances.
- **Concurrency**: deposits/withdrawals take a pessimistic row lock; all entities carry `@Version` for optimistic locking (conflicts → 409).
- **Balance** can only change via deposit/withdraw, never via `PUT`.
- **Schema** is managed by Flyway (`src/main/resources/db/migration`); Hibernate only validates it.
- IFSC (`^[A-Z]{4}0[A-Z0-9]{6}$`, normalized to upper case) and PIN code (6 digits, not starting with 0) are validated.

## Tests

```bash
mvn verify
```
