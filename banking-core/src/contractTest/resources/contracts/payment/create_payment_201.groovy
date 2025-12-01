package contracts.payment

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name "Create payment is successful and returns 201"
    request {
        method 'POST'
        urlPath '/payments'
        headers {
            contentType(applicationJson())
            accept(applicationJson())
        }
        body([
                fromCustomerId   : $(consumer(anyUuid())),
                fromAccountId    : $(consumer(anyUuid())),
                recipientIban    : $(anyNonBlankString()),
                amount           : [
                        amount  : $(consumer(regex('^(\\d+(\\.\\d+)?)$')), producer(1024.50)),
                        currency: $(consumer(regex('^(SEK|DKK|NOK|EUR)$')), producer('SEK'))
                ],
                scheduledDateTime: $(consumer(anyDateTime()))
        ])
    }
    response {
        status 201
        headers {
            header 'Content-Type': 'application/json'
        }
        body(
                id: $(producer(anyUuid())),
                fromAccountId: $(producer(anyUuid())),
                amount: [
                        amount  : $(consumer(1024.50), producer(regex('^(\\d+(\\.\\d+)?)$'))),
                        currency: $(consumer('SEK'), producer(regex('^(SEK|DKK|NOK|EUR)$')))
                ],
                recipientIban: $(producer(anyNonBlankString()))
        )
    }
}
