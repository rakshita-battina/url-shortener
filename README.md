# 📎 URL Shortener with Dashboard & QR Codes

A full-stack **Spring Boot + Angular** application to shorten URLs, generate QR codes, track analytics, and manage everything through a responsive dashboard.

---

## 🚀 Features

- 🔗 Shorten long URLs  
- ✍️ Custom short code support  
- ⏳ Expiration date for links  
- 📷 QR code generation & download  
- 📊 Dashboard with:  
  - Click count  
  - Creation date  
  - Edit/Delete options  
  - Pagination & sorting  
  - Click timeline visualization (Chart.js)  
- 🔌 REST API for integration  

---

## 📂 Project Structure

```
url-shortener/
├── backend/    # Spring Boot REST API
└── frontend/   # Angular application
```

---

## 🛠️ Backend Setup (Spring Boot)

### 1️⃣ Prerequisites

- Java **17+**
- Maven **3.8+**
- MySQL or PostgreSQL (or H2 for local testing)

### 2️⃣ Configure Database

Edit `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/urlshortener
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 3️⃣ Run Backend

```bash
cd backend
mvn spring-boot:run
```
Backend will be available at: [http://localhost:8080](http://localhost:8080)

---

## 🎨 Frontend Setup (Angular)

### 1️⃣ Prerequisites
- Node.js **18+**
- Angular CLI

### 2️⃣ Install Dependencies

```bash
cd frontend
npm install
```

### 3️⃣ Run Frontend

```bash
ng serve
```
Frontend will be available at: [http://localhost:4200](http://localhost:4200)

---

## 🔌 API Endpoints

### ➕ Create Short URL

`POST /api/shorten`

```json
{
  "originalUrl": "https://example.com",
  "customCode": "mycustom",
  "expiresAt": "2025-08-31T23:59:59"
}
```

**Response:**
```json
{
  "shortCode": "mycustom",
  "originalUrl": "https://example.com"
}
```

### 📄 Get All URLs

`GET /api/urls`

**Response:**
```json
[
  {
    "shortCode": "mycustom",
    "originalUrl": "https://example.com",
    "clickCount": 5,
    "createdAt": "2025-08-01T12:00:00"
  }
]
```

### ❌ Delete URL

`DELETE /api/url/{shortCode}`

---

## 📊 Dashboard Features

- Pagination: Configurable via `pageSize` in `app.component.ts`
- Sorting: By clicks, creation date, etc.
- Charts: Click count per day (Chart.js + ng2-charts)

---

## 📷 QR Code Support

- Show QR code for each shortened URL
- Download as PNG
- Powered by [angularx-qrcode](https://github.com/techiediaries/angularx-qrcode)

---

## 🧪 Testing with Postman

- ✅ Create short URL with and without custom code
- ⚠️ Handle duplicate custom codes (returns error)
- 🗑️ Delete shortened URL
- 📈 Fetch analytics data

---

## 🛠️ Troubleshooting

| Issue                             | Cause                                            | Fix                                  |
|------------------------------------|--------------------------------------------------|--------------------------------------|
| Custom short code not saving       | Not passed from frontend                         | Ensure `customCode` is sent in request |
| Cannot delete URL                  | Foreign key constraint from clicks table         | Enable cascade delete in entity mapping |
| Chart binding error in Angular     | Missing `NgChartsModule` import                  | Add `NgChartsModule` to imports array |

---

## 📄 License

MIT License – free to use and modify.

---

## 👨‍💻 Author

Built with ❤️ using Spring Boot & Angular.
