import type { PageLoad } from '../$types';
import type { CustomerList } from '../../../types';

export const load: PageLoad = async ({ fetch, params }) => {
  const res = await fetch('http://localhost:8083/customer/search', {
    method: 'GET',
    headers: {
      'Accept': 'application/json',
      "Content-Type": "application/json",
    }
  });
  return await res.json() as CustomerList;
}