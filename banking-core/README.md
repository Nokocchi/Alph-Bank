# Alph Bank Core

This service represents the banking core of Alph Bank. It consists of four fundamental parts

## Links

Swagger: http://localhost:8080/webjars/swagger-ui/index.html

Adminer DB page: http://localhost:5433/?pgsql=postgres&username=alph&db=core

## Customer

Examples of data points:

* Customer id
* National Identity Number
* Name
* Address
* Country & language

## Account

Examples of data points:

* Account id
* Account name
* Account IBAN
* Balance & currency

## Loan

Examples of data points:

* Loan id
* Customer id
* Account id
* Interest rate
* Loan period

## Payment

Examples of data points:

* Payment id
* To/from account and customer ids
* Amount & Currency
* Message
* Scheduled & Executed date-time



