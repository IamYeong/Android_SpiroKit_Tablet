package kr.co.theresearcher.spirokitfortab.main;

import java.util.Observable;

public class ProxyObserver extends Observable {

    public ProxyObserver() {
        setChanged();
    }

    public void notificationObservers(Object arg) {
        if (!hasChanged()) {
            setChanged();
        }
        notifyObservers(arg);
    }


}
