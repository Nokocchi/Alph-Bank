
import type { Account, AccountList } from '../../../../../../types';
import { ACCOUNT_SERVICE_URL } from '../../../../../globals';
import type { PageLoad } from '../../../account/[accountId]/$types';

export const load: PageLoad = async ({ fetch, params }) => {
  let queryParams: URLSearchParams = new URLSearchParams({ customer_id: params.customerId });

  const accountSearchRes = await fetch(ACCOUNT_SERVICE_URL + '/account/search?' + queryParams.toString(), {
    method: 'GET',
    headers: {
      'Accept': 'application/json',
      "Content-Type": "application/json",
    },
  });

  let accountList: AccountList = await accountSearchRes.json() as AccountList;

  return {
    customerId: params.customerId,
    selectedAccountId: params.accountId,
    accounts: accountList.accounts
  }
}