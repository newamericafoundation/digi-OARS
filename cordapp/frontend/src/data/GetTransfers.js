import axios from "axios";

const getRequests = async (port) => {
  const url =
    "http://" + window._env_.API_CLIENT_URL + ":" + port + "/api/transfers";

  const res = await axios.get(url);
  return res.data.entity.map((ob, index) => ({
    transferUsername: ob.transferUsername,
    issuanceParty: ob.issuanceParty,
    receivingDept: ob.receivingDept,
    authorizedUserUsername: ob.authorizedUserUsername,
    externalAccountId: ob.externalAccountId,
    linearId: ob.linearId.id,
    requestStateLinearId: ob.requestStateLinearId.id,
    currency: ob.currency,
    amount: ob.amount,
    participants: ob.participants,
    createDateTime: ob.datetime
  }));
};

export default getRequests;
