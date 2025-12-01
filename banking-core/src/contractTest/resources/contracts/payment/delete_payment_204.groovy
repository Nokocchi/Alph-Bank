package contracts.payment

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name "Delete payment is successful and returns 204"
    request {
        method 'DELETE'
        urlPath $(consumer(regex('/payments/' + uuid())))
    }
    response {
        status 204
    }
}
