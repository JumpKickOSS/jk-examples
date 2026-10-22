package dev.jkexamples.guestbook

/** One signature in the guestbook; GORM fills {@code dateCreated}. */
class Entry {

    String name
    String message
    Date dateCreated

    static constraints = {
        name nullable: false, blank: false, maxSize: 60
        message nullable: false, blank: false, maxSize: 500
    }

    static mapping = {
        sort dateCreated: 'desc'
    }
}
