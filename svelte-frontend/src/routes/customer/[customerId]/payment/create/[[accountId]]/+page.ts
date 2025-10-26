
import type { Account, AccountList, Basket, Customer } from '../../../../../../types';
import { ACCOUNT_SERVICE_URL, CUSTOMER_SERVICE_URL, PAYMENT_SERVICE_URL } from '../../../../../globals';
import type { PageLoad } from '../../../account/[accountId]/$types';

export const load: PageLoad = async ({ fetch, params }) => {
  let queryParams: URLSearchParams = new URLSearchParams({ "customer-id": params.customerId });

  const accountSearchRes = await fetch(ACCOUNT_SERVICE_URL + '/account/search?' + queryParams.toString(), {
    method: 'GET',
    headers: {
      'Accept': 'application/json',
      "Content-Type": "application/json",
    },
  });

  const basketSearchRes = await fetch(PAYMENT_SERVICE_URL + '/basket/search?' + queryParams.toString(), {
    method: 'GET',
    headers: {
      'Accept': 'application/json',
      "Content-Type": "application/json",
    },
  });

  const customerRes = await fetch(CUSTOMER_SERVICE_URL + '/customer/' + params.customerId, {
    method: 'GET',
    headers: {
      'Accept': 'application/json',
      "Content-Type": "application/json",
    },
  });

  let accountList: AccountList = await accountSearchRes.json() as AccountList;
  let basket;
  try {
    basket = await basketSearchRes.json() as Basket;
  } catch (error) {
  }
  let customer: Customer = await customerRes.json() as Customer;

  return {
    customer: customer,
    customerId: params.customerId,
    selectedAccountId: params.accountId,
    accounts: accountList.accounts,
    currentBasket: basket
  }
}