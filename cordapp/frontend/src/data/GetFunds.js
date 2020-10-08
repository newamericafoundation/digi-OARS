import axios from "axios";

const getFunds = async (port) => {
  const url =
    "http://" + window._env_.API_CLIENT_URL + ":" + port + "/api/funds";

  const res = await axios.get(url);

  return res.data.entity.map((ob) => ({
    linearId: ob.linearId.id,
    createdDateTime: ob.createDatetime,
    updatedDateTime: ob.updatedDateTime,
    originParty: ob.originParty,
    receivingParty: ob.receivingParty,
    currency: ob.currency,
    amount: ob.amount,
    balance: ob.balance,
    maxWithdrawalAmount: ob.maxWithdrawalAmount,
    status: ob.status,
    participants: ob.participants,
    receivedByUsername: ob.receivedByUsername
  }));
};

export default getFunds;
