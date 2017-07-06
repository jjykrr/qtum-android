package com.pixelplex.qtum.datastorage;


import android.content.Context;
import android.util.Log;

import com.google.common.collect.ImmutableList;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.WalletExtension;
import com.pixelplex.qtum.utils.CurrentNetParams;
import com.pixelplex.qtum.utils.DictionaryWords;
import org.spongycastle.util.encoders.Hex;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;


public class KeyStorage implements Serializable {

    private static KeyStorage sKeyStorage;
    private List<DeterministicKey> mDeterministicKeyList;
    private List<String> mAddressesList;
    private Wallet sWallet = null;
    private int sCurrentKeyPosition = 0;
    private File mFile;
    private final int ADDRESSES_COUNT = 100;

    public static void setUpKeyStorage(KeyStorage kStorage){
        sKeyStorage = kStorage;
    }

    public static KeyStorage getInstance() {
        if (sKeyStorage == null) {
            sKeyStorage = new KeyStorage();
        }
        return sKeyStorage;
    }

    private KeyStorage() {

    }

    public void setWallet(Wallet wallet) {
        this.sWallet = wallet;
    }

    public void clearKeyStorage() {
        sKeyStorage = null;
    }

    public Observable<Wallet> loadWalletFromFile(Context context) {
        mFile = new File(context.getFilesDir().getPath() + "/key_storage");
        return Observable.create(new Observable.OnSubscribe<Wallet>() {
            @Override
            public void call(Subscriber<? super Wallet> subscriber) {
                try {
                    sWallet = Wallet.loadFromFile(mFile, new WalletExtension() {
                        @Override
                        public String getWalletExtensionID() {
                            return null;
                        }

                        @Override
                        public boolean isWalletExtensionMandatory() {
                            return false;
                        }

                        @Override
                        public byte[] serializeWalletExtension() {
                            return new byte[0];
                        }

                        @Override
                        public void deserializeWalletExtension(Wallet containingWallet, byte[] data) throws Exception {

                        }
                    });
                } catch (UnreadableWalletException e) {
                    e.printStackTrace();
                }
                getKeyList(ADDRESSES_COUNT);
                subscriber.onNext(sWallet);
            }
        });
    }

    public Observable<Wallet> createWallet(final Context context) {
        mFile = new File(context.getFilesDir().getPath() + "/key_storage");
        return Observable.create(new Observable.OnSubscribe<Wallet>() {
            @Override
            public void call(Subscriber<? super Wallet> subscriber) {

                String seedString = "";
                List<String> seedList = new ArrayList<>();
                for (int i = 0; i < 11; i++) {
                    seedString += DictionaryWords.getRandomWord() + " ";
                    seedList.add(DictionaryWords.getRandomWord());
                }
                seedString += DictionaryWords.getRandomWord();

                String passphrase = "";
                DeterministicSeed seed = null;
                try {
                    seed = new DeterministicSeed(seedString, null, passphrase, DeterministicHierarchy.BIP32_STANDARDISATION_TIME_SECS);
                } catch (UnreadableWalletException e) {
                    e.printStackTrace();
                }
                if (seed != null) {
                    sWallet = Wallet.fromSeed(CurrentNetParams.getNetParams(), seed);

                }
                try {
                    sWallet.saveToFile(mFile);
                    getKeyList(ADDRESSES_COUNT);
                    subscriber.onNext(sWallet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                QtumSharedPreference.getInstance().saveSeed(context, seedString);
            }
        });
    }

    public Observable<Wallet> importWallet(final String seedString, final Context context) {
        mFile = new File(context.getFilesDir().getPath() + "/key_storage");
        return Observable.create(new Observable.OnSubscribe<Wallet>() {
            @Override
            public void call(Subscriber<? super Wallet> subscriber) {

                String passphrase = "";
                DeterministicSeed seed = null;
                try {
                    seed = new DeterministicSeed(seedString, null, passphrase, DeterministicHierarchy.BIP32_STANDARDISATION_TIME_SECS);

                } catch (UnreadableWalletException e) {
                    e.printStackTrace();
                }
                if (seed != null) {
                    sWallet = Wallet.fromSeed(CurrentNetParams.getNetParams(), seed);
                }
                try {
                    sWallet.saveToFile(mFile);
                    getKeyList(ADDRESSES_COUNT);
                    subscriber.onNext(sWallet);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                QtumSharedPreference.getInstance().saveSeed(context, seedString);
            }
        });
    }

    public List<DeterministicKey> getKeyList(int numberOfKeys) {
        if (mDeterministicKeyList == null) {
            mDeterministicKeyList = new ArrayList<>(numberOfKeys);
            mAddressesList = new ArrayList<>();
            List<ChildNumber> pathParent = new ArrayList<>();
            pathParent.add(new ChildNumber(0,true));
            pathParent.add(new ChildNumber(0,true));
            for (int i = 0; i < numberOfKeys; i++) {
                ImmutableList<ChildNumber> path = HDUtils.append(pathParent, new ChildNumber(i, true));
                DeterministicKey k = sWallet.getActiveKeyChain().getKeyByPath(path,true);
                mDeterministicKeyList.add(k);
                mAddressesList.add(k.toAddress(CurrentNetParams.getNetParams()).toString());
            }
        }
        return mDeterministicKeyList;
    }

    public String getCurrentAddress() {
        return getKeyList(ADDRESSES_COUNT).get(sCurrentKeyPosition).toAddress(CurrentNetParams.getNetParams()).toString();
    }

    public List<String> getAddresses() {
        return mAddressesList;
    }

    public DeterministicKey getCurrentKey() {
        return getKeyList(ADDRESSES_COUNT).get(sCurrentKeyPosition);
    }

    public void setCurrentKeyPosition(int currentKeyPosition) {
        sCurrentKeyPosition = currentKeyPosition;
    }

    public int getCurrentKeyPosition() {
        return sCurrentKeyPosition;
    }
}