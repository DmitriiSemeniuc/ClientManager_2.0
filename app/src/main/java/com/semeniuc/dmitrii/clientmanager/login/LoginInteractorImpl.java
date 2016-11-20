package com.semeniuc.dmitrii.clientmanager.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.semeniuc.dmitrii.clientmanager.App;
import com.semeniuc.dmitrii.clientmanager.data.local.DatabaseTaskHelper;
import com.semeniuc.dmitrii.clientmanager.model.User;
import com.semeniuc.dmitrii.clientmanager.utils.ActivityUtils;
import com.semeniuc.dmitrii.clientmanager.utils.Constants;
import com.semeniuc.dmitrii.clientmanager.utils.GoogleAuthenticator;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginInteractorImpl implements LoginInteractor {

    @Inject ActivityUtils utils;
    @Inject User user;
    @Inject DatabaseTaskHelper dbHelper;
    @Inject GoogleAuthenticator authenticator;

    private String email;

    public LoginInteractorImpl() {
        App.getInstance().getComponent().inject(this);
    }

    @Override
    public void loginWithGoogle(GoogleSignInResult result, OnGoogleLoginFinishedListener listener) {
        if (result.isSuccess()) {
            boolean userCreated = setGoogleUserDetails(result);
            if (userCreated) saveGoogleUser(listener);
        } else if (!utils.isNetworkAvailable())
            listener.onNoInternetAccess();
        else
            listener.onGoogleLoginError();
    }

    @Override
    public void loginWithEmail(String email, String password, OnLoginFinishedListener listener) {
        if (!isLoginFieldsValid(email, password, listener)) return;
        User user = dbHelper.getUserByEmailAndPassword(email, password);
        if (user != null) {
            setEmailUserDetails(user);
            utils.setUserInPrefs(Constants.REGISTERED_USER, this.user);
            listener.onSuccess();
        } else {
            listener.onInvalidCredentials();
        }
    }

    private void setEmailUserDetails(User user) {
        this.user.setId(user.getId());
        this.user.setEmail(user.getEmail());
        this.user.setPassword(user.getPassword());
    }

    private boolean isLoginFieldsValid(String email, String password, OnLoginFinishedListener listener) {
        boolean valid = true;
        if (TextUtils.isEmpty(email)) {
            listener.onUsernameError();
            valid = false;
        }
        if (TextUtils.isEmpty(password)) {
            listener.onPasswordError();
            valid = false;
        }
        return valid;
    }

    /**
     * Identify the user type
     * It can be: user signed in with google account or logged via e-mail
     */
    @Override public void verifyUserType(Context context, FragmentActivity activity,
                                         final OnVerifyUserTypeFinishedListener listener,
                                         LoginPresenter presenter) {
        String userType = utils.getUserFromPrefs();
        if (userType.equals(Constants.GOOGLE_USER)) {
            authenticator.setGoogleApiClient(context, activity);
            SharedPreferences settings = utils.getSharedPreferences(Constants.LOGIN_PREFS);
            boolean loggedIn = settings.getBoolean(Constants.LOGGED, false);
            if (loggedIn)
                silentSignInWithGoogle(authenticator.getOptionalPendingResult(), presenter);
            return;
        }
        if (userType.equals(Constants.REGISTERED_USER)) {
            SharedPreferences settings = utils.getSharedPreferences(Constants.LOGIN_PREFS);
            boolean loggedIn = settings.getBoolean(Constants.LOGGED, false);
            if (loggedIn) {
                email = settings.getString(Constants.EMAIL, Constants.EMPTY);
                if (!TextUtils.isEmpty(email)) setGlobalUser(listener);
            }
            return;
        }
        if (userType.equals(Constants.NEW_USER))
            authenticator.setGoogleApiClient(context, activity);
    }

    @Override
    public void silentSignInWithGoogle(final OptionalPendingResult<GoogleSignInResult> opr,
                                       final LoginPresenter presenter) {
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleGoogleSignInResult(result, presenter);
        } else {
            presenter.onShowProgressDialog();
            opr.setResultCallback(googleSignInResult -> {
                presenter.onHideProgressDialog();
                handleGoogleSignInResult(googleSignInResult, presenter);
            });
        }
    }

    /**
     * Handle Sign In result.
     * If result success -> go to Sign In Activity and finish this
     */
    private void handleGoogleSignInResult(GoogleSignInResult result,
                                          final LoginPresenter presenter) {
        if (!result.isSuccess()) return;
        GoogleSignInAccount account = result.getSignInAccount();
        if (account == null || dbHelper == null) return;
        User user = dbHelper.getUserByEmail(account.getEmail());
        Uri photoUrl = account.getPhotoUrl();
        if (null != user) {
            setEmailUserDetails(user);
            if (null != photoUrl) this.user.setPhotoUrl(photoUrl.toString());
            presenter.onUpdateUI();
        }
    }

    /**
     * Sets the signed user to global User object
     */
    private boolean setGoogleUserDetails(@NonNull GoogleSignInResult result) {
        GoogleSignInAccount account = result.getSignInAccount();
        if (null == account) return false;
        user.setGoogleId(account.getId());
        user.setName(account.getDisplayName());
        user.setEmail(account.getEmail());
        return true;
    }

    /**
     * Set Google user to Global user and save it to DB
     */
    private void saveGoogleUser(final OnGoogleLoginFinishedListener listener) {
        saveGoogleUserObservable.subscribe(new Subscriber<Integer>() {

            @Override
            public void onNext(Integer result) {
                if (result == Constants.USER_SAVED || result == Constants.USER_EXISTS) {
                    utils.setUserInPrefs(Constants.GOOGLE_USER, user);
                    listener.onGoogleLoginSuccess();
                }
                if (result == Constants.USER_NOT_SAVED || result == Constants.NO_DB_RESULT)
                    listener.onUserSavingFailed();
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                listener.onGoogleLoginError();
                e.getMessage();
            }
        });
    }

    final Observable<Integer> saveGoogleUserObservable = Observable.create(new Observable.OnSubscribe<Integer>() {
        @Override
        public void call(Subscriber<? super Integer> subscriber) {
            subscriber.onNext(dbHelper.saveGoogleUser(user));
            subscriber.onCompleted();
        }
    }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

    /**
     * Set global user for silent sign in
     */
    private void setGlobalUser(OnVerifyUserTypeFinishedListener listener) {
        setGlobalUserObservable.subscribe(new Subscriber<Integer>() {

            @Override
            public void onNext(Integer result) {
                if (result == Constants.USER_SAVED) {
                    User user = dbHelper.getUserByEmail(email);
                    if (null != user) setEmailUserDetails(user);
                    listener.onUserSaved();
                }
                if (result == Constants.USER_NOT_SAVED || result == Constants.NO_DB_RESULT)
                    listener.onUserSavingFailed();
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.getMessage();
            }
        });
    }

    final Observable<Integer> setGlobalUserObservable = Observable.create(new Observable.OnSubscribe<Integer>() {
        @Override
        public void call(Subscriber<? super Integer> subscriber) {
            subscriber.onNext(dbHelper.setGlobalUserWithEmail(email));
            subscriber.onCompleted();
        }
    }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

    @Override public void hideKeyboard(ViewGroup layout) {
        utils.hideKeyboard(layout);
    }
}
