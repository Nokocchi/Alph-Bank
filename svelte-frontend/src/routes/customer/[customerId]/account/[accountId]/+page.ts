import type { Account, CorePaymentSearchRestResponse } from '../../../../../types';
import { ACCOUNT_SERVICE_URL, CORE_PAYMENT_SERVICE_URL, PAYMENT_SERVICE_URL } from '../../../../globals';
import type { PageLoad } from './$types';

export const load: PageLoad = async ({ fetch, params }) => {
  const accountRes = await fetch(ACCOUNT_SERVICE_URL + '/account/' + params.accountId, {
    method: 'GET',
    headers: {
      'Accept': 'application/json',
      "Content-Type": "application/json",
    },
  });

  const account: Account = await accountRes.json() as Account;

  let queryParams: URLSearchParams = new URLSearchParams({ "from-account-id": params.accountId, "recipient-iban": account.iban });

  const paymentSearchRes = await fetch(CORE_PAYMENT_SERVICE_URL+ '/payment/search?' + queryParams.toString(), {
    method: 'GET',
    headers: {
      'Accept': 'application/json',
      "Content-Type": "application/json",
    },
  });
  
  const paymentList: CorePaymentSearchRestResponse = await paymentSearchRes.json() as CorePaymentSearchRestResponse;

  return {
    customerId: params.customerId,
    account: account,
    payments: paymentList.payments
  } 
}