
import type { Account } from '../../../../../../../types';
import { ACCOUNT_SERVICE_URL } from '../../../../../../globals';
import type { PageLoad } from './$types';

export const load: PageLoad = async ({ fetch, params }) => {
  const accountRes = await fetch(ACCOUNT_SERVICE_URL + '/account/' + params.accountId, {
    method: 'GET',
    headers: {
      'Accept': 'application/json',
      "Content-Type": "application/json",
    },
  });


  return {
    customerId: params.customerId,
    account: await accountRes.json() as Account
  } 
}