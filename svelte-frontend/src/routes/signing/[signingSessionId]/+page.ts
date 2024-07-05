import type { PageLoad } from './$types';
import type { SigningSession, UpdateSigningStatusRequest } from '../../../types';
import { SIGNING_SERVICE_URL } from '../../globals';
import { SigningStatusEnumSchema } from '../../schema';

export const load: PageLoad = async ({ fetch, params }) => {
  const signingSessionRes = await fetch(SIGNING_SERVICE_URL + '/signing/' + params.signingSessionId, {
    method: 'GET',
    headers: {
      'Accept': 'application/json',
      "Content-Type": "application/json",
    }
  });

  const updateSigningStatusRequest: UpdateSigningStatusRequest = {newStatus: SigningStatusEnumSchema.Enum.IN_PROGRESS};
  const signingStatusUpdatedResponse = await fetch(SIGNING_SERVICE_URL + "/signing/" + params.signingSessionId, {
    method: "PATCH",
    headers: {
      'Accept': 'application/json',
      "Content-Type": "application/json",
    },
    body: JSON.stringify(updateSigningStatusRequest)
  });

  return await signingSessionRes.json() as SigningSession;
}