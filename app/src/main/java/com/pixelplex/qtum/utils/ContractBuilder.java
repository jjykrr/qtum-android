package com.pixelplex.qtum.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.pixelplex.qtum.datastorage.FileStorageManager;
import com.pixelplex.qtum.model.contract.ContractMethod;
import com.pixelplex.qtum.model.contract.ContractMethodParameter;
import com.pixelplex.qtum.model.gson.UnspentOutput;
import com.pixelplex.qtum.datastorage.KeyStorage;
import com.pixelplex.qtum.utils.sha3.sha.Keccak;
import com.pixelplex.qtum.utils.sha3.sha.Parameters;

import org.bitcoinj.core.Base58;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptChunk;
import org.bitcoinj.script.ScriptOpCodes;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.spongycastle.util.encoders.Hex;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;


public class ContractBuilder {

    private String hashPattern = "0000000000000000000000000000000000000000000000000000000000000000";

                                //0000000000000000000000000000000000000000000000000000000000000001 //1 первый параметр
                                //0000000000000000000000000000000000000000000000000000000000000080 //128 оффсет первой строки
                                //0000000000000000000000000000000000000000000000000000000000000002 //2 третий параметр
                                //00000000000000000000000000000000000000000000000000000000000000c0 //192 оффсет второй строки
                                //0000000000000000000000000000000000000000000000000000000000000008 //8 длина первой строки
                                //6e616d6500000000000000000000000000000000000000000000000000000000 //name первая строка
                                //000000000000000000000000000000000000000000000000000000000000000c //12 длина второй строки
                                //73796d626f6c0000000000000000000000000000000000000000000000000000 //symbol вторая строка

    private final int radix = 16;
    private final String TYPE_INT = "int";
    private final String TYPE_STRING = "string";
    private final String TYPE_ADDRESS = "address";

    final int OP_PUSHDATA_1 = 1;
    final int OP_PUSHDATA_4 = 0x04;
    final int OP_PUSHDATA_8 = 8;
    final int OP_EXEC = 193;
    final int OP_EXEC_ASSIGN = 194;
    final int OP_EXEC_SPEND = 195;

    private Context mContext;

    public ContractBuilder(){

    }

    private List<ContractMethodParameter> mContractMethodParameterList;

    public Observable<String> createAbiMethodParams(final String _methodName, final List<ContractMethodParameter> contractMethodParameterList){
        return rx.Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                String methodName = _methodName;
                String parameters = "";
                String abiParams = "";
                mContractMethodParameterList = contractMethodParameterList;
                if(contractMethodParameterList != null && contractMethodParameterList.size()!=0) {
                    for (ContractMethodParameter parameter : contractMethodParameterList) {
                        abiParams += convertParameter(parameter,abiParams);
                        parameters = parameters + parameter.getType() + ",";
                    }
                    methodName = methodName + "("+parameters.substring(0,parameters.length()-1)+")";
                } else{
                    methodName = methodName + "()";
                }
                Keccak keccak = new Keccak();
                String hashMethod = keccak.getHash(Hex.toHexString((methodName).getBytes()), Parameters.KECCAK_256).substring(0,8);
                abiParams = hashMethod + abiParams;
                return abiParams;
            }
        });
    }

    long paramsCount;

    public Observable<String> createAbiConstructParams(final List<ContractMethodParameter> contractMethodParameterList, final String uiid, Context context){
        mContext = context;
        paramsCount = contractMethodParameterList.size();
        currStringOffset = 0;

        return rx.Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                String abiParams = "";
                mContractMethodParameterList = contractMethodParameterList;
                if(mContractMethodParameterList != null) {
                    for (ContractMethodParameter parameter : mContractMethodParameterList) {
                        abiParams += convertParameter(parameter, abiParams);
                    }
                }
                abiParams = getByteCodeByUiid(uiid) + abiParams + appendStringParameters();
                return abiParams;
            }
        });
    }

    private String convertParameter(ContractMethodParameter parameter, String abiParams) {

        String _value = parameter.getValue();

        if(parameter.getType().contains(TYPE_INT)){
            return appendNumericPattern(convertToByteCode(Long.valueOf(_value)));
        } else if(parameter.getType().contains(TYPE_STRING)){
            return  getStringOffset(parameter);
        } else if(parameter.getType().contains(TYPE_ADDRESS)){
            return appendAddressPattern(Hex.toHexString(Base58.decode(_value)).substring(2,42));
        }
        return "";
    }

    private String appendStringParameters(){
        String stringParams = "";
        for (ContractMethodParameter parameter : mContractMethodParameterList) {
            if(parameter.getType().contains(TYPE_STRING)){
                stringParams += appendStringPattern(convertToByteCode(parameter.getValue()));
            }
        }
        return stringParams;
    }

    private int getStringsChainSize(ContractMethodParameter parameter) {

        if(mContractMethodParameterList == null || mContractMethodParameterList.size() == 0){
            return 0;
        }

        int index = mContractMethodParameterList.indexOf(parameter);

        if(index == mContractMethodParameterList.size()-1) {
            return 1;
        }

        int chainSize = 0;

        for (int i = index; i< mContractMethodParameterList.size(); i++){
            if(mContractMethodParameterList.get(index).getType().contains(TYPE_STRING)){
                chainSize++;
            }
        }

        return chainSize;
    }

    private String convertToByteCode(long _value) {
        return Long.toString(_value,radix);
    }

    private static String convertToByteCode(String _value)
    {
        char[] chars = _value.toCharArray();
        StringBuilder hex = new StringBuilder();
        for (char aChar : chars) {
            hex.append(Integer.toHexString((int) aChar));
        }
        return hex.toString();
    }

    private String getStringOffset(String data) {
        return appendNumericPattern(convertToByteCode(data.length()));
    }

    long currStringOffset = 0;

    private String getStringOffset(ContractMethodParameter parameter){
        long currOffset = ((paramsCount + currStringOffset) * 32);
        currStringOffset = getStringHash(parameter.getValue()).length() / hashPattern.length() + 1/*string length section*/;
        return appendNumericPattern(convertToByteCode(currOffset));
    }

    private String getStringLength(String _value) {
        return appendNumericPattern(convertToByteCode(_value.length()));
    }

    private String getStringHash(String _value){
        if(_value.length()<=hashPattern.length()) {
            return formNotFullString(_value);
        }else {
            int ost = _value.length() % hashPattern.length();
            return  _value + hashPattern.substring(0,hashPattern.length()-ost);
        }
    }

    private String appendStringPattern(String _value) {

        String fullParameter = "";
        fullParameter += getStringLength(_value);

        if(_value.length()<=hashPattern.length()) {
            fullParameter += formNotFullString(_value);
        }else {
            int ost = _value.length() % hashPattern.length();
            fullParameter += _value + hashPattern.substring(0,hashPattern.length()-ost);
        }

        return fullParameter;
    }

    private String appendAddressPattern(String _value){
        return hashPattern.substring(_value.length()) + _value;
    }

    private String formNotFullString(String _value) {
        return _value + hashPattern.substring(_value.length());
    }

    private String appendNumericPattern(String _value){
        return hashPattern.substring(0,hashPattern.length()-_value.length()) + _value;
    }

    private String getByteCodeByUiid(String uiid) {
        return FileStorageManager.getInstance().readByteCodeContract(mContext, uiid);
    }

    public Script createConstructScript(String abiParams){

        byte[] version = Hex.decode("01000000");
        byte[] gasLimit = Hex.decode("80841e0000000000");
        byte[] gasPrice = Hex.decode("0100000000000000");
        byte[] data = Hex.decode(abiParams);
        byte[] program;

        ScriptChunk versionChunk = new ScriptChunk(OP_PUSHDATA_4,version);
        ScriptChunk gasLimitChunk = new ScriptChunk(OP_PUSHDATA_8,gasLimit);
        ScriptChunk gasPriceChunk = new ScriptChunk(OP_PUSHDATA_8,gasPrice);
        ScriptChunk dataChunk = new ScriptChunk(ScriptOpCodes.OP_PUSHDATA2,data);
        ScriptChunk opExecChunk = new ScriptChunk(OP_EXEC, null);
        List<ScriptChunk> chunkList = new ArrayList<>();
        chunkList.add(versionChunk);
        chunkList.add(gasLimitChunk);
        chunkList.add(gasPriceChunk);
        chunkList.add(dataChunk);
        chunkList.add(opExecChunk);

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            for (ScriptChunk chunk : chunkList) {
                chunk.write(bos);
            }
            program = bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Script(program);
    }

    public Script createMethodScript(String abiParams, String _contractAddress) throws RuntimeException{

        byte[] version = Hex.decode("01000000");
        byte[] gasLimit = Hex.decode("80841e0000000000");
        byte[] gasPrice = Hex.decode("0100000000000000");
        byte[] data = Hex.decode(abiParams);
        byte[] contractAddress = Hex.decode(_contractAddress);
        byte[] program;

        ScriptChunk versionChunk = new ScriptChunk(OP_PUSHDATA_4,version);
        ScriptChunk gasLimitChunk = new ScriptChunk(OP_PUSHDATA_8,gasLimit);
        ScriptChunk gasPriceChunk = new ScriptChunk(OP_PUSHDATA_8,gasPrice);
        ScriptChunk dataChunk = new ScriptChunk(ScriptOpCodes.OP_PUSHDATA2,data);
        ScriptChunk contactAddressChunk = new ScriptChunk(ScriptOpCodes.OP_PUSHDATA2,contractAddress);
        ScriptChunk opExecChunk = new ScriptChunk(OP_EXEC_ASSIGN, null);
        List<ScriptChunk> chunkList = new ArrayList<>();
        chunkList.add(versionChunk);
        chunkList.add(gasLimitChunk);
        chunkList.add(gasPriceChunk);
        chunkList.add(dataChunk);
        chunkList.add(contactAddressChunk);
        chunkList.add(opExecChunk);

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            for (ScriptChunk chunk : chunkList) {
                chunk.write(bos);
            }
            program = bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Script(program);
    }

    public String createTransactionHash(Script script, List<UnspentOutput> unspentOutputs){

        Transaction transaction = new Transaction(CurrentNetParams.getNetParams());
        transaction.addOutput(Coin.ZERO,script);

        UnspentOutput unspentOutput = null;
        for(UnspentOutput unspentOutput1: unspentOutputs) {
            if(unspentOutput1.getAmount().doubleValue()>1.0) {
                unspentOutput = unspentOutput1;
                break;
            }
        }

        if(unspentOutput == null){
            throw new RuntimeException("You have insufficient funds for this transaction");
        }

        BigDecimal bitcoin = new BigDecimal(100000000);
        ECKey myKey = KeyStorage.getInstance().getCurrentKey();
        transaction.addOutput(Coin.valueOf((long)(unspentOutput.getAmount().multiply(bitcoin).subtract(new BigDecimal("10000000")).doubleValue())),
                myKey.toAddress(CurrentNetParams.getNetParams()));

        for (DeterministicKey deterministicKey : KeyStorage.getInstance().getKeyList(10)) {
            if (Hex.toHexString(deterministicKey.getPubKeyHash()).equals(unspentOutput.getPubkeyHash())) {
                Sha256Hash sha256Hash = new Sha256Hash(Utils.parseAsHexOrBase58(unspentOutput.getTxHash()));
                TransactionOutPoint outPoint = new TransactionOutPoint(CurrentNetParams.getNetParams(), unspentOutput.getVout(), sha256Hash);
                Script script2 = new Script(Utils.parseAsHexOrBase58(unspentOutput.getTxoutScriptPubKey()));
                transaction.addSignedInput(outPoint, script2, deterministicKey, Transaction.SigHash.ALL, true);
                break;
            }
        }

        transaction.getConfidence().setSource(TransactionConfidence.Source.SELF);
        transaction.setPurpose(Transaction.Purpose.USER_PAYMENT);

        byte[] bytes = transaction.unsafeBitcoinSerialize();
        return Hex.toHexString(bytes);
    }


    private static String FUNCTION_TYPE = "function";
    private static String TYPE = "type";
    private List<ContractMethod> standardInterface;

    public boolean checkForValidity(String abiCode) {

        initStandardInterface();

        JSONArray array;
        List<ContractMethod> contractMethods = new ArrayList<>();
        try {
            array = new JSONArray(abiCode);

            for (int i = 0; i < array.length(); i++) {
                JSONObject jb = array.getJSONObject(i);
                if (FUNCTION_TYPE.equals(jb.getString(TYPE))) {
                    Gson gson = new Gson();
                    contractMethods.add(gson.fromJson(jb.toString(), ContractMethod.class));
                }
            }

            boolean validityFlag = true;
            for (ContractMethod contractMethodStandard : standardInterface) {
                for (ContractMethod contractMethod : contractMethods) {
                    if (contractMethod.getName().equals(contractMethodStandard.getName()) && contractMethod.getType().contains(contractMethodStandard.getType()) && contractMethod.isConstant()==contractMethodStandard.isConstant()) {
                        if (contractMethod.getInputParams() != null && contractMethodStandard.getInputParams() != null) {
                            for (ContractMethodParameter contractMethodParameterStandard : contractMethodStandard.getInputParams()) {
                                for (ContractMethodParameter contractMethodParameter : contractMethod.getInputParams()) {
                                    if (contractMethodParameter.getName().equals(contractMethodParameterStandard.getName()) && contractMethodParameter.getType().contains(contractMethodParameterStandard.getType())) {
                                        validityFlag = true;
                                        break;
                                    }
                                    validityFlag = false;
                                }
                                if(!validityFlag) return false;
                            }
                        }
                        if (contractMethod.getOutputParams() != null && contractMethodStandard.getOutputParams() != null) {
                            for (ContractMethodParameter contractMethodParameterStandard : contractMethodStandard.getOutputParams()) {
                                for (ContractMethodParameter contractMethodParameter : contractMethod.getOutputParams()) {
                                    if (contractMethodParameter.getName().equals(contractMethodParameterStandard.getName()) && contractMethodParameter.getType().contains(contractMethodParameterStandard.getType())) {
                                        validityFlag = true;
                                        break;
                                    }
                                    validityFlag = false;
                                }
                                if(!validityFlag) return false;
                            }
                        }
                        validityFlag = true;
                        break;
                    }
                    validityFlag = false;
                }
                if(!validityFlag) return false;
            }
            return true;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void initStandardInterface(){
        if(standardInterface==null){
            standardInterface = new ArrayList<>();

            List<ContractMethodParameter> totalSupplyOutputParams = new ArrayList<>();
            totalSupplyOutputParams.add(new ContractMethodParameter("totalSupply","uint"));
            ContractMethod totalSupply = new ContractMethod(true,"function",null,"totalSupply",totalSupplyOutputParams);

            List<ContractMethodParameter> balanceOfInputParams = new ArrayList<>();
            balanceOfInputParams.add(new ContractMethodParameter("_owner","address"));
            List<ContractMethodParameter> balanceOfOutputParams = new ArrayList<>();
            balanceOfOutputParams.add(new ContractMethodParameter("balance","uint"));
            ContractMethod balanceOfSupply = new ContractMethod(true,"function",balanceOfInputParams,"balanceOf",balanceOfOutputParams);

            List<ContractMethodParameter> transferInputParams = new ArrayList<>();
            transferInputParams.add(new ContractMethodParameter("_to","address"));
            transferInputParams.add(new ContractMethodParameter("_value","uint"));
            List<ContractMethodParameter> transferOutputParams = new ArrayList<>();
            transferOutputParams.add(new ContractMethodParameter("success","bool"));
            ContractMethod transfer = new ContractMethod(false,"function",transferInputParams,"transfer",transferOutputParams);

            List<ContractMethodParameter> transferFromInputParams = new ArrayList<>();
            transferFromInputParams.add(new ContractMethodParameter("_from","address"));
            transferFromInputParams.add(new ContractMethodParameter("_to","address"));
            transferFromInputParams.add(new ContractMethodParameter("_value","uint"));
            List<ContractMethodParameter> transferFromOutputParams = new ArrayList<>();
            transferFromOutputParams.add(new ContractMethodParameter("success","bool"));
            ContractMethod transferFrom = new ContractMethod(false,"function",transferFromInputParams,"transferFrom",transferFromOutputParams);

            List<ContractMethodParameter> approveInputParams = new ArrayList<>();
            approveInputParams.add(new ContractMethodParameter("_spender","address"));
            approveInputParams.add(new ContractMethodParameter("_value","uint"));
            List<ContractMethodParameter> approveOutputParams = new ArrayList<>();
            approveOutputParams.add(new ContractMethodParameter("success","bool"));
            ContractMethod approve = new ContractMethod(false,"function",approveInputParams,"approve",approveOutputParams);

            List<ContractMethodParameter> allowanceInputParams = new ArrayList<>();
            allowanceInputParams.add(new ContractMethodParameter("_owner","address"));
            allowanceInputParams.add(new ContractMethodParameter("_spender","address"));
            List<ContractMethodParameter> allowanceOutputParams = new ArrayList<>();
            allowanceOutputParams.add(new ContractMethodParameter("remaining","uint"));
            ContractMethod allowance = new ContractMethod(true,"function",allowanceInputParams,"allowance",allowanceOutputParams);

            standardInterface.add(totalSupply);
            standardInterface.add(balanceOfSupply);
            standardInterface.add(transfer);
            standardInterface.add(transferFrom);
            standardInterface.add(approve);
            standardInterface.add(allowance);
        }
    }

}
