import axios from "axios";

const getPartialRequests = async (port) => {
  const url =
    "http://" + window._env_.API_CLIENT_URL + ":" + port + "/api/partial/requests";

  const res = await axios.get(url);
  return res.data.entity.map((ob, index) => ({
    authorizedUserDept: ob.authorizedUserDept,
    authorizedParties: ob.authorizedParties,
    linearId: ob.linearId.id,
    fundStateLinearId: ob.fundStateLinearId.id,
    currency: ob.currency,
    amount: ob.amount,
    participants: ob.participants,
    createDateTime: ob.datetime,
    purpose: ob.purpose
  }));
};

export default getPartialRequests;
