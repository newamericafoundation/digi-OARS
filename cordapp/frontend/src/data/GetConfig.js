import axios from "axios";

const getConfig = async (port) => {
  const url =
    "http://" + window._env_.API_CLIENT_URL + ":" + port + "/api/config";

  const res = await axios.get(url)
  
  return res.data.entity.map((ob) => ({
    linearId: ob.linearId.id,
    creator: ob.creator,
    country: ob.country,
    currency: ob.currency,
    maxWithdrawalAmount: ob.maxWithdrawalAmount,
    createDateTime: ob.createDatetime
  }));
};

export default getConfig;
