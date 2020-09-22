import axios from "axios";

const getPartialRequests = async (port) => {
  const url =
    "http://" + window._env_.API_CLIENT_URL + ":" + port + "/api/partial/requests";

  const res = await axios.get(url);
  return res.data.entity.map((ob, index) => ({
    authorizedUserDept: ob.state.data.authorizedUserDept,
    authorizedParties: ob.state.data.authorizedParties,
    linearId: ob.state.data.linearId.id,
    fundStateLinearId: ob.state.data.fundStateLinearId.id,
    currency: ob.state.data.currency,
    amount: ob.state.data.amount,
    participants: ob.state.data.participants,
    txId: ob.ref.txhash,
    createDateTime: ob.state.data.datetime,
  }));
};

export default getPartialRequests;
