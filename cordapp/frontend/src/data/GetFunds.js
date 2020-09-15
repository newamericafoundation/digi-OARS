import axios from "axios";

const getFunds = async (port) => {
  const url =
    "http://" + window._env_.API_CLIENT_URL + ":" + port + "/api/funds";

  const res = await axios.get(url);
  return res.data.entity.map((ob, index) => ({
    linearId: ob.state.data.linearId.id,
    createdDateTime: ob.state.data.createDatetime,
    updatedDateTime: ob.state.data.updatedDateTime,
    originParty: ob.state.data.originParty,
    receivingParty: ob.state.data.receivingParty,
    currency: ob.state.data.currency,
    amount: ob.state.data.amount,
    balance: ob.state.data.balance,
    maxWithdrawalAmount: ob.state.data.maxWithdrawalAmount,
    status: ob.state.data.status,
    participants: ob.state.data.participants,
    txId: ob.ref.txhash,
  }));
};

export default getFunds;
