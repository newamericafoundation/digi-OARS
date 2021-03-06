import axios from "axios";

const getRequestsByFundId = async (port, fundId) => {
  const url =
    "http://" + window._env_.API_CLIENT_URL + ":" + port + "/api/requests";

  const res = await axios.get(url, { params: { fundId: fundId } });

  return res.data.entity.map((ob, index) => ({
    authorizedUserUsername: ob.authorizedUserUsername,
    authorizedUserDept: ob.authorizedUserDept,
    authorizerUserDeptAndUsername: ob.authorizerUserDeptAndUsername,
    authorizedParties: ob.authorizedParties,
    linearId: ob.linearId.id,
    fundStateLinearId: ob.fundStateLinearId.id,
    externalAccountId: ob.externalAccountId,
    purpose: ob.purpose,
    currency: ob.currency,
    amount: ob.amount,
    status: ob.status,
    participants: ob.participants,
    createDateTime: ob.createDatetime,
    updateDateTime: ob.updateDateTime
  }));
};

export default getRequestsByFundId;
