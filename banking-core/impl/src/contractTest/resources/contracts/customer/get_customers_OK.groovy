package contracts.customer

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    priority 1
    request {
        method 'GET'
        urlPath '/customers/search'
        headers {
            contentType(applicationJson())
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
                                id         : "509c0b04-b1f4-4b72-bac9-3e50e9fbcee4",
                                address    : [
                                        streetAddress: "street",
                                        city         : "city",
                                        country      : "country"
                                ],
                                nationalId : "nationalId",
                                firstName  : "firstName",
                                lastName   : "lastName",
                                language   : "sv",
                                countryCode: "SE"
                        ]
                ]
        )
    }
}
