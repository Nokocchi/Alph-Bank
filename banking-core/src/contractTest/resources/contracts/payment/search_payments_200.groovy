package contracts.payment

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name "Search payments is successful and returns 200"
    request {
        method 'GET'
        urlPath('/payments/search') {
            queryParameters {
                parameter 'customer-id': $(consumer(anyUuid()))
            }
        }
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
                [
                        [
                                id           : $(producer(anyUuid())),
                                fromAccountId: $(producer(anyUuid())),
                                amount       : [
                                        amount  : $(consumer(1024.50), producer(regex('^(\\d+(\\.\\d+)?)$'))),
                                        currency: $(consumer('SEK'), producer(regex('^(SEK|DKK|NOK|EUR)$')))
                                ],
                                recipientIban: $(producer(anyNonBlankString()))
                        ]
                ]
        )
    }
}
