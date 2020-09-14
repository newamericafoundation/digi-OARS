import axios from "axios";

const getRequests = async (port) => {
  const url =
    "http://" + window._env_.API_CLIENT_URL + ":" + port + "/api/requests";

  const res = await axios.get(url);
  return res.data.entity.map((ob, index) => ({
    authorizedUserUsername: ob.state.data.authorizedUserUsername,
    authorizedUserDept: ob.state.data.authorizedUserDept,
    authorizerUserUsername: ob.state.data.authorizerUserUsername,
    authorizedParties: ob.state.data.authorizedParties,
    linearId: ob.state.data.linearId.id,
    fundStateLinearId: ob.state.data.fundStateLinearId.id,
    externalAccountId: ob.state.data.externalAccountId,
    purpose: ob.state.data.purpose,
    currency: ob.state.data.currency,
    amount: ob.state.data.amount,
    status: ob.state.data.status,
    participants: ob.state.data.participants,
    txId: ob.ref.txhash,
  }));
};

export default getRequests;
