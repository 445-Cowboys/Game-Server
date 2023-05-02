package org.server;

//The sole purpose of this thread is to make sure a player has made their move within the allotted time
//so long as the values aren't null (meaning no game has been initiated)
//this watcher will run a future that has a time out of 30 seconds that will return true or null
//if it returns true, it means the person made their turn
//if it is null, it means the player is likely gone from the game and we can get rid of them.
//we'll also have the future return false, meaning the person didn't go but only because they exited gracefully from
//the game, so we need not wait for the previous player to make their move anymore.
public class TurnWatcher implements Runnable{
    @Override
    public void run() {

    }
}
