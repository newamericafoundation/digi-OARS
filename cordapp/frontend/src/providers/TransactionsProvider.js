import React, {
    createContext,
    useEffect,
    useReducer,
    useCallback,
    useContext,
  } from "react";
  import getTransactions from "../data/GetTransactions";
  import { APIContext } from "./APIProvider";
  
  export const TransactionsContext = createContext();
  
  const initialState = {
    data: [],
    loading: true,
  };
  
  const reducer = (state, action) => {
    switch (action.type) {
      case "GET_TRANSACTIONS":
        return {
          data: action.payload,
          loading: false,
        };
      default:
        return state;
    }
  };
  
  const TransactionsProvider = ({ children }) => {
    const [state, dispatch] = useReducer(reducer, initialState);
    const [api] = useContext(APIContext);
  
  
    const callback = useCallback(
      () => {
        getTransactions(api.port).then((data) => {
          dispatch({ type: "GET_TRANSACTIONS", payload: data });
        })
      },
      [dispatch, api.port],
    )
  
    useEffect(() => {
      if (api.port) {
        getTransactions(api.port).then((data) => {
          dispatch({ type: "GET_TRANSACTIONS", payload: data });
        });
      }
    }, [api.port]);
  
    return (
      <TransactionsContext.Provider value={[state, callback]}>
        {children}
      </TransactionsContext.Provider>
    );
  };
  
  export default TransactionsProvider;
  