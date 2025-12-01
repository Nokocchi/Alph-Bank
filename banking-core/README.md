# Alph Bank Core

This service represents the banking core of Alph Bank. It consists of four fundamental parts:

* Customers
* Accounts
* Payments
* Loans

In a real world scenario, you will probably have a SaaS Core Banking Platform, or more likely an older mainframe with software written in COBOL, RPG or similar.

For demonstration purposes, the core banking platform in Alph Bank has been implemented as a single Java Spring Boot application with a Postgres database.

## Features

### Double entry bookkeeping & account transactions:

[Example](/banking-core/src/main/java/com/alphbank/core/shared/TransactionService.java)

As is common practice in the banking sector, the fundamental principles of transactions and balances in Alph Bank
are that:

- All changes to the balance of an account is defined as two separate transactions in the database:
A positive transaction for the creditor, and a negative transaction for the debtor. 
- Transactions cannot be created via the API. The API only supports creating payments and loans, which the Alph Bank BankingCore
takes care of turning into transactions.
- Accounts do not store their balance. Instead, the balance is calculated by adding the sum of all transactions to the 
initial balance of the account when it was opened.
- This theoretically allows Alph Bank to run periodic checks on all transactions to verify that the sum of all transactions equals to 0, 
or that for every transaction there is indeed an equal but opposite transaction on another account.

In reality, this is much harder to implement due to payments to other banks, failed payments, international payments,
currency conversions, loans, pending payments, race conditions on multiple simultaneous transactions and so on.

The Alph Bank implementation is therefore just a subset of the required logic for handling all edge cases.


## Links

Swagger: http://localhost:8080/webjars/swagger-ui/index.html

Adminer DB page: http://localhost:5433/?pgsql=postgres&username=alph&db=core
