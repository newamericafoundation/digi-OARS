import React, { createContext, useState, useEffect } from "react";
import axios from "axios";

export const FundsContext = createContext();

const { Provider } = FundsContext;

const FundsProvider = ({ children }) => {
  const [state, setState] = useState([]);
  const [load, setLoad] = useState(false);
  const [error, setError] = useState('');

  const url =
    "http://" +
    window._env_.API_CLIENT_URL +
    ":" +
    window._env_.API_CLIENT_PORT +
    "/api/funds";

  useEffect(() => {
    const fetchData = async () => {
      await axios.get(url).then((response) => {
        const stateData = response.data.entity.map((ob, index) => ({
          linearId: ob.state.data.linearId.id,
          dateTime: ob.state.data.datetime,
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
        setState(stateData);
        setLoad(true);
      }).catch(err => {
        setError(err.message);
        setLoad(true);
      });
    };
    
    fetchData();
    
  }, [url]);

  return <Provider value={[state, setState]}>{children}</Provider>;
};

FundsProvider.context = FundsContext;

export default FundsProvider;
