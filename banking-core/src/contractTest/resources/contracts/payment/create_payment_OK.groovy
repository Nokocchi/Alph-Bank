package contracts.payment

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    priority 1
    request {
        method 'POST'
        urlPath '/payment'
        headers {
            contentType(applicationJson())
        }
        body([
                fromCustomerId    : "509c0b04-b1f4-4b72-bac9-3e50e9fbcee4",
                fromAccountId     : "509c0b04-b1f4-4b72-bac9-3e50e9fbcee4",
                recipientIban     : "string",
                amount            : [
                        amount  : 22,
                        currency: "SEK"
                ],
                messageToSelf     : "string",
                messageToRecipient: "string",
                scheduledDateTime: "2015-08-04T10:11:30"
        ])
    }
    response {
        status 201
        headers {
            header 'Content-Type': 'application/json'
        }
        body(
                paymentId: "509c0b04-b1f4-4b72-bac9-3e50e9fbcee4",
                fromCustomerId: "509c0b04-b1f4-4b72-bac9-3e50e9fbcee4",
                fromAccountId: "509c0b04-b1f4-4b72-bac9-3e50e9fbcee4",
                amount: [
                        amount  : "22",
                        currency: "SEK"
                ],
                recipientIban: "string",
                recipientAccountId: "509c0b04-b1f4-4b72-bac9-3e50e9fbcee4",
                messageToSelf: "string",
                messageToRecipient: "string"
        )
    }
}
