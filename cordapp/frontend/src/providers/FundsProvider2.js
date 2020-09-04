// import React, { createContext, useState, useEffect, useReducer } from "react";
// import axios from "axios";
// import getFunds from '../data/GetFunds';


// export const FundsContext = createContext();

// // const { Provider } = FundsContext;

// const initialState = {
//   data: [],
//   loading: true
// };

// const reducer = (state, action) => {
//   switch (action.type) {
//     case "GET_FUNDS":
//       return {
//         data: [action.payload],
//         loading: false
//       };
//     default:
//       return state;
//   }
// };



// const FundsProvider = ({ children }) => {
//   const [state, dispatch] = useReducer(reducer, initialState)
//   console.log(state)

//   const [dataState, setDataState] = useState([]);
//   const [error, setError] = useState("");

//   const url =
//     "http://" +
//     window._env_.API_CLIENT_URL +
//     ":" +
//     window._env_.API_CLIENT_PORT +
//     "/api/funds";

//   useEffect(() => {
//     // const data = getFunds.then(item);
//     // console.log(data)
//     dispatch({type: 'GET_FUNDS', payload: getFunds().then(item => [item])})
//     // const fetchData = async () => {
//     //   await axios
//     //     .get(url)
//     //     .then((response) => {
//     //       const stateData = response.data.entity.map((ob, index) => ({
//     //         linearId: ob.state.data.linearId.id,
//     //         dateTime: ob.state.data.datetime,
//     //         originParty: ob.state.data.originParty,
//     //         receivingParty: ob.state.data.receivingParty,
//     //         currency: ob.state.data.currency,
//     //         amount: ob.state.data.amount,
//     //         balance: ob.state.data.balance,
//     //         maxWithdrawalAmount: ob.state.data.maxWithdrawalAmount,
//     //         status: ob.state.data.status,
//     //         participants: ob.state.data.participants,
//     //         txId: ob.ref.txhash,
//     //       }));
//     //       console.log(stateData)
          
//     //       setDataState(stateData);
//     //     })
//     //     .catch((err) => {
//     //       setError(err.message);
//     //     });
//     // };

//     // fetchData();

//     return () => {};
//   }, [url]);

//   return (
//   <FundsContext.Provider value={[state, dispatch]}>{children}</FundsContext.Provider>
//   );
// };

// // FundsProvider.context = FundsContext;

// export default FundsProvider;
