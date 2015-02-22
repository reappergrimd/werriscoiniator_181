package com.nitinsurana.bitcoinlitecoin.rpcconnector;

//import com.nitinsurana.litecoinrpcconnector.responses.JSONResponse;
//import com.nitinsurana.litecoinrpcconnector.responses.ArrayResponse;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.files.ConcurrentConfig;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Hello world!
 *
 */
public class RPCApp {

     public static final Logger LOG = Logger.getLogger(RPCApp.class);

//    static final String rpcUser = "Nitin";
//    static final String rpcPassword = "magicmaker07";
//    static final String rpcHost = "localhost";
//    static final String rpcPort = "9332";
    WebClient client;
    String baseUrl;
    private RPCApp myself;
    private String walletPasshrase = "";
    private boolean havePasshrase = false;

    public RPCApp(String rpcUser, String rpcPassword, String rpcHost, String rpcPort, String walletPasshrase) throws AuthenticationException {
        client = new WebClient(BrowserVersion.FIREFOX_17);
        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
        client.getOptions().setThrowExceptionOnScriptError(false);
        client.getOptions().setPrintContentOnFailingStatusCode(false);
        client.getOptions().setJavaScriptEnabled(false);
        client.getOptions().setCssEnabled(false);

        baseUrl = new String("http://" + rpcUser + ":" + rpcPassword + "@" + rpcHost + ":" + rpcPort + "/");
        LOG.info("Base RPC URL : " + baseUrl);

        try {
            if (client.getPage(baseUrl).getWebResponse().getStatusCode() == 401) {  //401 is Http Unauthorized
                throw new AuthenticationException();
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
        if(walletPasshrase != null && !walletPasshrase.isEmpty() && !walletPasshrase.equals(" "))
        {
            havePasshrase = true;
            this.walletPasshrase = walletPasshrase;
        }
        myself = this;
    }

    /**
     * Safely copies wallet.dat to destination, which can be a directory or a
     * path with filename.
     *
     * @param destination
     * @return
     * @throws Exception
     */
    public boolean backupWallet(String destination) throws Exception {
        JsonObject jsonObj = callAPIMethod(APICalls.BACKUP_WALLET, destination);

//        ArrayResponse response = new Gson().fromJson(responseString, ArrayResponse.class);
//        LOG.info("Backup Wallet : " + ToStringBuilder.reflectionToString(response, ToStringStyle.DEFAULT_STYLE));
//        if (response.getError() == null) {
        if (jsonObj.get("error") == null) {
            return true;
        }
        return false;
    }

    /**
     * Produces a human-readable JSON object for a raw transaction.
     *
     * @param hex
     * @return
     * @throws Exception
     */
    public JsonObject decodeRawTransaction(String hex) throws Exception {
        JsonObject jsonObj = callAPIMethod(APICalls.DECODE_RAW_TRANSACTION, hex);

//        ArrayResponse response = new Gson().fromJson(responseString, ArrayResponse.class);
//        LOG.info("Decode Raw Transaction : " + ToStringBuilder.reflectionToString(response, ToStringStyle.DEFAULT_STYLE));
        if (jsonObj.get("error") != null && jsonObj.get("error").isJsonObject() == true) {
            String message = jsonObj.get("error").getAsJsonObject().get("message").getAsString();
            throw new RpcInvalidResponseException(message);
        }

        return jsonObj.get("result").getAsJsonObject();
    }

    /**
     * Reveals the private key corresponding to <address>
     *
     * @param address
     * @return
     * @throws Exception
     */
    public String dumpPrivateKey(String address) throws Exception {
        JsonObject jsonObj = callAPIMethod(APICalls.DUMP_PRIVATE_KEY, address);

        if (jsonObj.get("error") != null && jsonObj.get("error").isJsonObject() == true) {
            String message = jsonObj.get("error").getAsJsonObject().get("message").getAsString();
            throw new RpcInvalidResponseException(message);
        }
        return jsonObj.get("result").getAsString();
    }

    /**
     * Returns raw transaction representation for given transaction id.
     *
     * @param txid
     * @return returns the hex string for the given transaction id
     * @throws Exception
     */
    public String getRawTransaction(String txid) throws Exception {
        JsonObject jsonObj = callAPIMethod(APICalls.GET_RAW_TRANSACTION, txid);

        if (jsonObj.get("error") != null && jsonObj.get("error").isJsonObject() == true) {
            String message = jsonObj.get("error").getAsJsonObject().get("message").getAsString();
            throw new RpcInvalidResponseException(message);
        }
        return jsonObj.get("result").getAsString();
    }

    /**
     * Encrypts the wallet with <passphrase>.
     *
     * @param passphrase
     * @return
     * @throws Exception
     */
//    public String encryptWallet(String passphrase) throws Exception {
//        JsonObject jsonObj = callAPIMethod(APICalls.ENCRYPT_WALLET, passphrase);
//
//        if (jsonObj.get("error") != null && jsonObj.get("error").isJsonObject() == true) {
//            String message = jsonObj.get("error").getAsJsonObject().get("message").getAsString();
//            throw new RpcInvalidResponseException(message);
//        }
//        return jsonObj.get("result").getAsString();
//    }
    /**
     * Returns the account associated with the given address.
     *
     * @param address
     * @return
     * @throws Exception
     */
    public String getAccount(String address) throws Exception {
        JsonObject jsonObj = callAPIMethod(APICalls.GET_ACCOUNT, address);

        if (jsonObj.get("error") != null && jsonObj.get("error").isJsonObject() == true) {
            String message = jsonObj.get("error").getAsJsonObject().get("message").getAsString();
            throw new RpcInvalidResponseException(message);
        }
        return jsonObj.get("result").getAsString();
    }

    /**
     * Returns the current Litecoin address for receiving payments to this
     * account.
     *
     * @param account
     * @return
     * @throws Exception
     */
    public String getAccountAddress(String account) throws Exception {
        JsonObject jsonObj = callAPIMethod(APICalls.GET_ACCOUNT_ADDRESS, account);

        if (jsonObj.get("error") != null && jsonObj.get("error").isJsonObject() == true) {
            String message = jsonObj.get("error").getAsJsonObject().get("message").getAsString();
            throw new RpcInvalidResponseException(message);
        }
        return jsonObj.get("result").getAsString();
    }

    /**
     * Returns the list of addresses for the given account.
     *
     * @param account
     * @return
     * @throws Exception
     */
    public JsonArray getAddressesByAccount(String account) throws Exception {
        JsonObject jsonObj = callAPIMethod(APICalls.GET_ADDRESSES_BY_ACCOUNT, account);

        if (jsonObj.get("error") != null && jsonObj.get("error").isJsonObject() == true) {
            String message = jsonObj.get("error").getAsJsonObject().get("message").getAsString();
            throw new RpcInvalidResponseException(message);
        }
        return jsonObj.get("result").getAsJsonArray();
    }

    /**
     * Returns the balance in the account.
     *
     * @param account
     * @return
     * @throws Exception
     */
    public double getBalance(String account) throws Exception {
        JsonObject jsonObj = callAPIMethod(APICalls.GET_BALANCE, account);

        if (jsonObj.get("error") != null && jsonObj.get("error").isJsonObject() == true) {
            String message = jsonObj.get("error").getAsJsonObject().get("message").getAsString();
            throw new RpcInvalidResponseException(message);
        }
        return jsonObj.get("result").getAsDouble();
    }

    /**
     * Returns the server's total available balance.
     *
     * @return
     * @throws Exception
     */
    public double getBalance() throws Exception {
        JsonObject jsonObj = callAPIMethod(APICalls.GET_BALANCE);

        if (jsonObj.get("error") != null && jsonObj.get("error").isJsonObject() == true) {
            String message = jsonObj.get("error").getAsJsonObject().get("message").getAsString();
            throw new RpcInvalidResponseException(message);
        }
        return jsonObj.get("result").getAsDouble();
    }

    /**
     * return will include all transactions to all accounts
     *
     * @return
     * @throws Exception
     */
//    public double getReceivedByAccount() throws Exception {
//        JsonObject jsonObj = callAPIMethod(APICalls.GET_RECEIVED_BY_ACCOUNT);
//        
//        if (jsonObj.get("error") != null && jsonObj.get("error").isJsonObject() == true) {
//            String message = jsonObj.get("error").getAsJsonObject().get("message").getAsString();
//            throw new RpcInvalidResponseException(message);
//        }
//        return jsonObj.get("result").getAsDouble();
//    }
    /**
     * Returns the total amount received by addresses with [account] in
     * transactions
     *
     * @param account
     * @return
     * @throws Exception
     */
    public double getReceivedByAccount(String account) throws Exception {
        JsonObject jsonObj = callAPIMethod(APICalls.GET_RECEIVED_BY_ACCOUNT, account);

        if (jsonObj.get("error") != null && jsonObj.get("error").isJsonObject() == true) {
            String message = jsonObj.get("error").getAsJsonObject().get("message").getAsString();
            throw new RpcInvalidResponseException(message);
        }
        return jsonObj.get("result").getAsDouble();
    }

    /**
     * Returns a new address for receiving payments.
     *
     * @return
     * @throws Exception
     */
    public String getNewAddress() throws Exception {
        JsonObject jsonObj = callAPIMethod(APICalls.GET_NEW_ADDRESS);

        if (jsonObj.get("error") != null && jsonObj.get("error").isJsonObject() == true) {
            String message = jsonObj.get("error").getAsJsonObject().get("message").getAsString();
            throw new RpcInvalidResponseException(message);
        }
        return jsonObj.get("result").getAsString();
    }

    /**
     * Returns a new address for receiving payments.
     *
     * @return
     * @throws Exception
     */
    public String getNewAddress(String account) throws Exception {
        JsonObject jsonObj = callAPIMethod(APICalls.GET_NEW_ADDRESS, account);

        if (jsonObj.get("error") != null && jsonObj.get("error").isJsonObject() == true) {
            String message = jsonObj.get("error").getAsJsonObject().get("message").getAsString();
            throw new RpcInvalidResponseException(message);
        }
        return jsonObj.get("result").getAsString();
    }

    /**
     * Returns the total amount received by <address> in transactions
     *
     * @param address
     * @return
     * @throws Exception
     */
    public double getReceivedByAddress(String address) throws Exception {
        JsonObject jsonObj = callAPIMethod(APICalls.GET_RECEIVED_BY_ADDRESS, address);

        if (jsonObj.get("error") != null && jsonObj.get("error").isJsonObject() == true) {
            String message = jsonObj.get("error").getAsJsonObject().get("message").getAsString();
            throw new RpcInvalidResponseException(message);
        }
        return jsonObj.get("result").getAsDouble();
    }

    /**
     * Returns an object about the given transaction containing: amount,
     * confirmations, txid, time[1], details (an array of objects containing:
     * account, address, category, amount, fee)
     *
     * @param txid
     * @return
     * @throws Exception
     */
    public JsonObject getTransaction(String txid) throws Exception {
        JsonObject jsonObj = callAPIMethod(APICalls.GET_TRANSACTION, txid);

        if (jsonObj.get("error") != null && jsonObj.get("error").isJsonObject() == true) {
            String message = jsonObj.get("error").getAsJsonObject().get("message").getAsString();
            throw new RpcInvalidResponseException(message);
        }
        return jsonObj.get("result").getAsJsonObject();
    }

    /**
     * Returns Object that has account names as keys, account balances as
     * values.
     *
     * @return
     * @throws Exception
     */
    public JsonObject listAccounts() throws Exception {
        JsonObject jsonObj = callAPIMethod(APICalls.LIST_ACCOUNTS);

        if (jsonObj.get("error") != null && jsonObj.get("error").isJsonObject() == true) {
            String message = jsonObj.get("error").getAsJsonObject().get("message").getAsString();
            throw new RpcInvalidResponseException(message);
        }
        return jsonObj.get("result").getAsJsonObject();
    }

    /**
     * Returns an array of objects containing: account, amount, confirmations
     *
     * @return
     */
    public JsonArray listReceivedByAccount() throws Exception {
        JsonObject jsonObj = callAPIMethod(APICalls.LIST_RECEIVED_BY_ACCOUNT);

        if (jsonObj.get("error") != null && jsonObj.get("error").isJsonObject() == true) {
            String message = jsonObj.get("error").getAsJsonObject().get("message").getAsString();
            throw new RpcInvalidResponseException(message);
        }
        return jsonObj.get("result").getAsJsonArray();
    }

    /**
     * Returns an array of objects containing: address, account, amount,
     * confirmations
     *
     * @return
     * @throws Exception
     */
    public JsonArray listReceivedByAddress() throws Exception {
        JsonObject jsonObj = callAPIMethod(APICalls.LIST_RECEIVED_BY_ADDRESS);

        if (jsonObj.get("error") != null && jsonObj.get("error").isJsonObject() == true) {
            String message = jsonObj.get("error").getAsJsonObject().get("message").getAsString();
            throw new RpcInvalidResponseException(message);
        }
        return jsonObj.get("result").getAsJsonArray();
    }

    /**
     * <amount> is a real and is rounded to 8 decimal places. Will send the
     * given amount to the given address, ensuring the account has a valid
     * balance using [minconf] confirmations. Returns the transaction ID if
     * successful
     *
     * @param fromAccount
     * @param toAddress
     * @param amount
     * @return
     * @throws Exception
     */
     public String sendFrom(String fromAccount, String toAddress, double amount) throws Exception {
        if(havePasshrase) 
        {
            walletpassphrase(walletPasshrase, 500000);
        }
         
        JsonObject jsonObj = callAPIMethod(APICalls.SEND_FROM, fromAccount, toAddress, amount);

        if (jsonObj.get("error") != null && jsonObj.get("error").isJsonObject() == true) {
            String message = jsonObj.get("error").getAsJsonObject().get("message").getAsString();
            throw new RpcInvalidResponseException(message);
        }
        return jsonObj.get("result").getAsString();
    }

    /**
     * Sets the account associated with the given address. Assigning address
     * that is already assigned to the same account will create a new address
     * associated with that account.
     *
     * @param address
     * @param account
     * @return
     * @throws Exception
     */
    public void setAccount(String address, String account) throws Exception {
        JsonObject jsonObj = callAPIMethod(APICalls.SET_ACCOUNT, address, account);

        if (jsonObj.get("error") != null && jsonObj.get("error").isJsonObject() == true) {
            String message = jsonObj.get("error").getAsJsonObject().get("message").getAsString();
            throw new RpcInvalidResponseException(message);
        }
//        return jsonObj.get("result").getAsString();
    }

    /**
     * Returns up to [count] most recent transactions skipping the first [from]
     * transactions for account [account].
     *
     * @param account
     * @param count
     * @param from
     * @return
     * @throws Exception
     */
    public JsonArray listTransactions(String account, int count, int from) throws Exception {
        JsonObject jsonObj = callAPIMethod(APICalls.LIST_TRANSACTIONS, account, count, from);

        if (jsonObj.get("error") != null && jsonObj.get("error").isJsonObject() == true) {
            String message = jsonObj.get("error").getAsJsonObject().get("message").getAsString();
            throw new RpcInvalidResponseException(message);
        }
        return jsonObj.get("result").getAsJsonArray();
    }
    
    public boolean move(String fromAccount, String toAccount, double amount) throws Exception {
        JsonObject jsonObj = callAPIMethod(APICalls.MOVE, fromAccount, toAccount, amount);

        if (jsonObj.get("error") != null && jsonObj.get("error").isJsonObject() == true) {
            String message = jsonObj.get("error").getAsJsonObject().get("message").getAsString();
            throw new RpcInvalidResponseException(message);
        }
        return Boolean.parseBoolean(jsonObj.get("result").getAsString());
    }
    
    public boolean walletpassphrase(String walletpasshrase, int timeout) throws Exception {
        
        JsonObject jsonObj = callAPIMethod(APICalls.WALLET_PASS_PHRASE, walletpasshrase, timeout);
        
        if (jsonObj.get("error") != null && jsonObj.get("error").isJsonObject() == true) {
            return false;
        }
        return true;
    }
    
    public boolean sendtoaddress(String toAddress, double amount) throws Exception {
        if(havePasshrase) 
        {
            walletpassphrase(walletPasshrase, 500000);
        }
        
        JsonObject jsonObj = callAPIMethod(APICalls.SEND_TO_ADDRESS, toAddress, amount);

        if (jsonObj.get("error") != null && jsonObj.get("error").isJsonObject() == true) {
            return false;
        }
        return true;
    }

    public Boolean isAccountInList(String account) throws Exception
    {
        return myself.listAccounts().has(account);
    }
    
    public static RPCApp getAppOutRPCconf(String conf) throws Exception {
        File file = new File(conf);
        ConcurrentConfig concurrentConfig = new ConcurrentConfig(file);
        concurrentConfig.load(file, "=");
        Map<String, String> copyOfProperties = concurrentConfig.getCopyOfProperties();
        String rpcUser = "BITQUARK";
        String rpcPassword = "PASSWORD";
        String rpcHost = "localhost";
        String rpcPort = "9108";
        String walletPassphrase = " ";
        if(copyOfProperties.isEmpty())
        {
            copyOfProperties.put("rpcUser", rpcUser);
            copyOfProperties.put("rpcPassword", rpcPassword);
            copyOfProperties.put("rpcHost", rpcHost);
            copyOfProperties.put("rpcPort", rpcPort);
            copyOfProperties.put("walletPassphrase", walletPassphrase);
            concurrentConfig.update(copyOfProperties);
            concurrentConfig.save("=");
        } else {
            rpcUser = copyOfProperties.get("rpcUser");
            rpcPassword = copyOfProperties.get("rpcPassword");
            rpcHost = copyOfProperties.get("rpcHost");
            rpcPort = copyOfProperties.get("rpcPort");
            walletPassphrase = copyOfProperties.get("walletPassphrase");
        }
       
        RPCApp app = new RPCApp(rpcUser, rpcPassword, rpcHost, rpcPort,walletPassphrase);
        return app;
    }
        
    public static void main(String[] args) throws Exception {
        String myrpcconf = "myrpc.conf";
        RPCApp app = getAppOutRPCconf(myrpcconf);
        boolean sendtoaddress = app.sendtoaddress("14uzNKcV5ZkyCU4mZWuu3GAo83gwZFfgjY", 0.01);
        System.out.println(sendtoaddress);
    }

    private JsonObject callAPIMethod(APICalls callMethod, Object... params) throws Exception {
        JsonObject jsonObj = null;
//        JSONResponse jsonResponse = null;
        WebRequest req = new WebRequest(new URL(baseUrl));
        req.setAdditionalHeader("Content-type", "application/json");
        req.setHttpMethod(HttpMethod.POST);
        JSONRequestBody body = new JSONRequestBody();
//        body.setMethod("getnewaddress");
        body.setMethod(callMethod.toString());
        if (params != null && params.length > 0) {
            body.setParams(params);
        }
        req.setRequestBody(new Gson().toJson(body, JSONRequestBody.class));
        WebResponse resp = client.getPage(req).getWebResponse();
        jsonObj = new JsonParser().parse(resp.getContentAsString()).getAsJsonObject();
//            jsonResponse = new Gson().fromJson(responseString, JSONResponse.class);
        LOG.info("RPC Response : " + jsonObj);
//        return jsonResponse.getResult();
//        return jsonResponse;
        return jsonObj;
    }
}
