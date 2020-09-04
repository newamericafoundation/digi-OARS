// import React from "react";
// import axios from "axios";

// const url =
//   "http://" +
//   window._env_.API_CLIENT_URL +
//   ":" +
//   window._env_.API_CLIENT_PORT +
//   "/api/funds";

// const getFunds = () => {
//    return axios.get(url).then((res) => {
//     return res.data.entity.map((ob, index) => ({
//       linearId: ob.state.data.linearId.id,
//       dateTime: ob.state.data.datetime,
//       originParty: ob.state.data.originParty,
//       receivingParty: ob.state.data.receivingParty,
//       currency: ob.state.data.currency,
//       amount: ob.state.data.amount,
//       balance: ob.state.data.balance,
//       maxWithdrawalAmount: ob.state.data.maxWithdrawalAmount,
//       status: ob.state.data.status,
//       participants: ob.state.data.participants,
//       txId: ob.ref.txhash,
//     }));
//   });
// };

// export default getFunds;
