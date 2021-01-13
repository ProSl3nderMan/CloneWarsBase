package me.prosl3nderman.clonewarsbase.Internal.Exceptions;

// thrown whenever a method is not ran async but should be ran async.
// for an example, all luck perms methods inside of LuckPermsAPI.java should be ran async.

public class NotAsynchronousException extends RuntimeException {

    public NotAsynchronousException(String message) {
        super(message);
    }
}
