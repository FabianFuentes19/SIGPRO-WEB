# SIGPRO-WEB

Monorepo para la aplicación web SIGPRO (backend + frontend).

## Estructura

```
SIGPRO-WEB/
├── backend/          # Spring Boot 3, Java 17, Maven, Oracle JDBC + JPA
├── frontend/         # React + Vite
└── README.md
```

## Backend (Spring Boot)

- **Java 17**, **Maven**
- **Spring Boot 3.2**, Spring Web, Spring Data JPA
- **Oracle JDBC** (ojdbc11) y librerías de **Wallet** (oraclepki, osdt_cert, osdt_core)
- Base de datos **Oracle** con Wallet en `src/main/resources/wallet`

### Requisitos

- JDK 17
- Maven 3.8+

### Configuración Oracle Wallet

1. Descarga el Wallet de tu instancia Oracle (Autonomous Database o similar).
2. Coloca los archivos en `backend/src/main/resources/wallet/`:
   - `tnsnames.ora`
   - `sqlnet.ora`
   - `cwallet.sso` (y opcionalmente `ewallet.pem` / `ewallet.p12`)
3. Define las variables de entorno o edita `application.properties`:
   - `ORACLE_TNS_ALIAS`: alias del servicio en `tnsnames.ora` (ej. `mydb_high`)
   - `ORACLE_USER` y `ORACLE_PASSWORD`: usuario y contraseña

### Ejecutar

```bash
cd backend
mvn spring-boot:run
```

API en `http://localhost:8080`.

---

## Frontend (React + Vite)

- **React 19**, **Vite 7**

### Requisitos

- Node.js 18+

### Ejecutar

```bash
cd frontend
npm install
npm run dev
```

App en `http://localhost:5173`.

- `npm run build` — build de producción
- `npm run preview` — previsualizar el build

---

## Desarrollo local

1. Arrancar Oracle (o usar cloud) y configurar el Wallet en `backend/src/main/resources/wallet`.
2. En una terminal: `cd backend && mvn spring-boot:run`.
3. En otra: `cd frontend && npm run dev`.
4. Frontend en **5173**, backend en **8080** (configura el proxy en Vite si necesitas llamar al API).
