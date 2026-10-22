package dev.jkexamples.guestbook

import grails.gorm.transactions.Transactional

/** The guestbook page: every entry, newest first, and the form to sign it. */
class EntryController {

    static allowedMethods = [save: 'POST']

    def index() {
        [entries: Entry.list(), entry: new Entry()]
    }

    @Transactional
    def save() {
        Entry entry = new Entry(name: params.name, message: params.message)
        if (!entry.save(flush: true)) {
            render view: 'index', model: [entries: Entry.list(), entry: entry]
            return
        }
        flash.message = "Thanks for signing, ${entry.name}!"
        redirect action: 'index'
    }
}
