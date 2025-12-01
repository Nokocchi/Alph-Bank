package contracts.payment

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name "Get payment is successful and returns 200"
    request {
        method 'GET'
        urlPath $(consumer(regex('/payments/' + uuid())))
        headers {
            accept(applicationJson())
        }
    }
    response {
        status 200
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
