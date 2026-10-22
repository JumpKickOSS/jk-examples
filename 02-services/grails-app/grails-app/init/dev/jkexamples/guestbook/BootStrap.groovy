package dev.jkexamples.guestbook

class BootStrap {

    def init = { servletContext ->
        Entry.withTransaction {
            if (Entry.count() == 0) {
                new Entry(name: 'Ada', message: 'Built with jk, no Gradle in sight.').save(flush: true)
            }
        }
    }

    def destroy = {
    }
}
