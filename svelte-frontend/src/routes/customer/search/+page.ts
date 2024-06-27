import type { PageLoad } from '../$types';
import type { CustomerList } from '../../../types';
import { CUSTOMER_SERVICE_URL } from '../../globals';

export const load: PageLoad = async ({ fetch, params }) => {
  const res = await fetch(CUSTOMER_SERVICE_URL + '/customer/search', {
    method: 'GET',
    headers: {
      'Accept': 'application/json',
      "Content-Type": "application/json",
    }
  });
  return await res.json() as CustomerList;
}