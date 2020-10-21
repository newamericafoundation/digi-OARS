import axios from "axios";

const getRequests = async (port) => {
  const url =
    "http://" + window._env_.API_CLIENT_URL + ":" + port + "/api/requests";

  const res = await axios.get(url);

  return res.data.entity.map((ob, index) => ({
    transferUsername: ob.transferUsername,
    authorizedUserUsername: ob.authorizedUserUsername,
    authorizedUserDept: ob.authorizedUserDept,
    authorizerUserDeptAndUsername: ob.authorizerUserDeptAndUsername,
    authorizedParties: ob.authorizedParties,
    linearId: ob.linearId.id,
    externalAccountId: ob.externalAccountId,
    purpose: ob.purpose,
    currency: ob.currency,
    amount: ob.amount,
    status: ob.status,
    participants: ob.participants,
    createDateTime: ob.createDatetime,
    updateDateTime: ob.updateDatetime,
    maxWithdrawalAmount: ob.maxWithdrawalAmount,
    rejectReason: ob.rejectReason
  }));
};

export default getRequests;
