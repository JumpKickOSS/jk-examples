package dev.jkexamples.guestbook

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class EntrySpec extends Specification implements DomainUnitTest<Entry> {

    void "a name and a message are required"() {
        expect:
        !new Entry(name: '', message: 'hi').validate()
        !new Entry(name: 'Ada', message: '').validate()
        new Entry(name: 'Ada', message: 'hi').validate()
    }

    void "a message is at most 500 characters"() {
        expect:
        !new Entry(name: 'Ada', message: 'x' * 501).validate(['message'])
    }
}
