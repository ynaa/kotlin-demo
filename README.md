# kotlin-demo

Run docker-compose up -d

Start bank-db, run main.kt
Start bank-front, run main.kt

front-url:
- get http://localhost:8080/front/customers
- post http://localhost:8080/front/customers
- get http://localhost:8080/front/accounts/1
- get http://localhost:8080/front/transactionsIn/92345678910
- get http://localhost:8080/front/transactionsOut/12345678910
- post http://localhost:8080/front/transfer

Docs:
- Exposed: https://github.com/JetBrains/Exposed
- Ktor: https://ktor.io/
- Kotlin, getting started docs: https://kotlinlang.org/docs/home.html