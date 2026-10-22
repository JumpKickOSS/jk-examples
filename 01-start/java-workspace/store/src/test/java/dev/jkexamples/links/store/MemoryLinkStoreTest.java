package dev.jkexamples.links.store;

import dev.jkexamples.links.LinkStore;
import dev.jkexamples.links.LinkStoreContract;
import java.time.Clock;

class MemoryLinkStoreTest extends LinkStoreContract {

    @Override
    protected LinkStore newStore() {
        return new MemoryLinkStore(Clock.systemUTC());
    }
}
