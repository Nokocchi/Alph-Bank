import type { AccountList, Customer } from '../../../types';
import { ACCOUNT_SERVICE_URL } from '../../globals';
import type { PageLoad } from './$types';

export const load: PageLoad = async ({ fetch, params }) => {
  const customerRes = await fetch('http://localhost:8083/customer/' + params.customerId, {
    method: 'GET',
    headers: {
      'Accept': 'application/json',
      "Content-Type": "application/json",
    },
  });

  let queryParams: URLSearchParams = new URLSearchParams({ customer_id: params.customerId });

  const accountSearchRes = await fetch(ACCOUNT_SERVICE_URL + '/account/search?' + queryParams.toString(), {
    method: 'GET',
    headers: {
      'Accept': 'application/json',
      "Content-Type": "application/json",
    },
  });


  return {
    customer: await customerRes.json() as Customer,
    accountList: await accountSearchRes.json() as AccountList,
  }
}