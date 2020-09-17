import axios from "axios";

const getRequests = async (port) => {
  const url =
    "http://" + window._env_.API_CLIENT_URL + ":" + port + "/api/transfers";

  const res = await axios.get(url);
  return res.data.entity.map((ob, index) => ({
    issuanceParty: ob.state.data.issuanceParty,
    receivingDept: ob.state.data.receivingDept,
    authorizedUserUsername: ob.state.data.authorizedUserUsername,
    externalAccountId: ob.state.data.externalAccountId,
    linearId: ob.state.data.linearId.id,
    requestStateLinearId: ob.state.data.requestStateLinearId.id,
    currency: ob.state.data.currency,
    amount: ob.state.data.amount,
    participants: ob.state.data.participants,
    txId: ob.ref.txhash,
    createDateTime: ob.state.data.datetime
  }));
};

export default getRequests;
