
import type { Customer, SearchLoanApplicationsResponse, SearchLoansResponse } from '../../../../types';
import { CUSTOMER_SERVICE_URL, LOAN_APPLICATION_SERVICE_URL, LOAN_SERVICE_URL } from '../../../globals';
import type { PageLoad } from '../$types';

export const load: PageLoad = async ({ fetch, params }) => {

  let queryParams: URLSearchParams = new URLSearchParams({ customerId: params.customerId });

  const loanApplicationsResponse = await fetch(LOAN_APPLICATION_SERVICE_URL + '/loan_application/search?' + queryParams.toString(), {
    method: 'GET',
    headers: {
      'Accept': 'application/json',
      "Content-Type": "application/json",
    },
  });


  const approvedLoansResponse = await fetch(LOAN_SERVICE_URL + '/loan/search?' + queryParams.toString(), {
    method: 'GET',
    headers: {
      'Accept': 'application/json',
      "Content-Type": "application/json",
    },
  });


  let loanApplicationList: SearchLoanApplicationsResponse = await loanApplicationsResponse.json() as SearchLoanApplicationsResponse;
  let approvedLoanList: SearchLoansResponse = await approvedLoansResponse.json() as SearchLoansResponse;

  return {
    loanApplications: loanApplicationList.loanApplications,
    approvedLoans: approvedLoanList.loans
  }
}