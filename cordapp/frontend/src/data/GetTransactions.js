import axios from "axios";

const getTransactions = async (port) => {
  const url =
    "http://" + window._env_.API_CLIENT_URL + ":" + port + "/api/transactions";

  const res = await axios.get(url);

  if (res.data.entity.transactionData) {
    return res.data.entity.transactionData.map((ob) => ({
      transactionId: ob.transactionId,
      inputs: ob.inputs,
      inputTypes: ob.inputTypes,
      outputs: ob.outputs,
      outputTypes: ob.outputTypes,
      signers: ob.signers,
      notary: ob.notary,
      commands: ob.commands,
    }));
  }
};

export default getTransactions;
