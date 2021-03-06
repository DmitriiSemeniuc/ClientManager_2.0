package com.semeniuc.dmitrii.clientmanager.registr;

import android.text.TextUtils;
import android.view.ViewGroup;

import com.semeniuc.dmitrii.clientmanager.App;
import com.semeniuc.dmitrii.clientmanager.data.local.DatabaseTaskHelper;
import com.semeniuc.dmitrii.clientmanager.model.User;
import com.semeniuc.dmitrii.clientmanager.utils.ActivityUtils;
import com.semeniuc.dmitrii.clientmanager.utils.Const;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RegistrationInteractorImpl implements RegistrationInteractor {

    @Inject ActivityUtils utils;
    @Inject User user;
    @Inject DatabaseTaskHelper dbHelper;

    public RegistrationInteractorImpl() {
        App.getInstance().getComponent().inject(this);
    }

    @Override
    public void registerWithEmail(String email, String password, String confirmPassword,
                                  OnRegistrationFinishedListener listener) {
        if (!isRegistrationFormValid(email, password, confirmPassword, listener)) return;
        setRegisteredUserDetails(email, password);
        saveRegisteredUser(listener);
    }

    private void setRegisteredUserDetails(String email, String password) {
        user.setEmail(email);
        user.setPassword(password);
    }

    private boolean isRegistrationFormValid(String email, String password, String confirmPassword,
                                            OnRegistrationFinishedListener listener) {
        if (!isRegistrationFieldsValid(email, password, confirmPassword, listener)) return false;
        User userTemp = dbHelper.getUserByEmail(email);
        if (userTemp != null) {
            listener.onEmailRegisteredError();
            return false;
        }
        return true;
    }

    private boolean isRegistrationFieldsValid(String email, String password, String confirmPassword, OnRegistrationFinishedListener listener) {
        boolean valid = true;
        if (TextUtils.isEmpty(email)) {
            listener.onUserNameError();
            valid = false;
        }
        if (TextUtils.isEmpty(password)) {
            listener.onPasswordError();
            valid = false;
        }
        if (!TextUtils.equals(password, confirmPassword)) {
            listener.onPasswordDoesNotMatch();
            valid = false;
        }
        return valid;
    }

    private void saveRegisteredUser(OnRegistrationFinishedListener listener) {
        saveRegisteredUserObservable.subscribe(new Subscriber<Integer>() {

            @Override
            public void onNext(Integer result) {
                if (result == Const.Action.USER_SAVED) {
                    utils.setUserInPrefs(Const.UserType.REGISTERED, user);
                    listener.onSuccess();
                } else
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

    final Observable<Integer> saveRegisteredUserObservable = Observable.create(new Observable.OnSubscribe<Integer>() {
        @Override
        public void call(Subscriber<? super Integer> subscriber) {
            subscriber.onNext(dbHelper.saveRegisteredUser(user));
            subscriber.onCompleted();
        }
    }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

    @Override public void hideKeyboard(ViewGroup layout) {
        utils.hideKeyboard(layout);
    }
}
