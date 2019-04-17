package com.example.android.rxjava_training_1;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
  private static final String TAG=MainActivity.class.getSimpleName();
  private Disposable disposable;
    /**
     * Simple Observable emitting multiple Notes
     * -
     * Observable : Observer
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Observable<Note> noteObservable=getObservable();
        Observer<Note> noteObserver=getNotesObserver();
        noteObservable.subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribeWith(noteObserver);
    }
    private Observer<Note> getNotesObserver(){
        return new Observer<Note>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG,"onSubscribe");
                disposable=d;
            }

            @Override
            public void onNext(Note note) {
               Log.d(TAG,"onNext"+note.getNote());
            }

            @Override
            public void onError(Throwable e) {
             Log.d(TAG,"error"+e.getMessage());
            }

            @Override
            public void onComplete() {
           Log.d(TAG,"onComplete");
            }
        };
    }
    private Observable<Note>getObservable(){
        final List<Note>notes=prepareNote();
        return Observable.create(new ObservableOnSubscribe<Note>() {
            @Override
            public void subscribe(ObservableEmitter<Note> emitter) throws Exception {
            for (Note note:notes) {
                if (!emitter.isDisposed()) {
                    emitter.onNext(note);
                }
            }
                if(!emitter.isDisposed()){
                    emitter.onComplete();
                }
            }
        });
    }

    private List<Note> prepareNote() {
        List<Note> notes = new ArrayList<>();
        notes.add(new Note(1, "Buy tooth paste!"));
        notes.add(new Note(2, "Call brother!"));
        notes.add(new Note(3, "Watch Narcos tonight!"));
        notes.add(new Note(4, "Pay power bill!"));
        return notes;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}
