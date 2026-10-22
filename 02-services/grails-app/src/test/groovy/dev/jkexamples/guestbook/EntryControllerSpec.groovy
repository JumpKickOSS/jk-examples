package dev.jkexamples.guestbook

import grails.testing.gorm.DataTest
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class EntryControllerSpec extends Specification implements ControllerUnitTest<EntryController>, DataTest {

    void setupSpec() {
        mockDomain Entry
    }

    void "the page lists every entry"() {
        given:
        new Entry(name: 'Ada', message: 'first').save(flush: true)
        new Entry(name: 'Grace', message: 'second').save(flush: true)

        when:
        def model = controller.index()

        then:
        model.entries*.name.sort() == ['Ada', 'Grace']
        model.entry instanceof Entry
    }

    void "signing saves the entry and thanks the guest"() {
        when:
        request.method = 'POST'
        params.name = 'Linus'
        params.message = 'hello'
        controller.save()

        then:
        Entry.count() == 1
        flash.message == 'Thanks for signing, Linus!'
        response.redirectedUrl == '/entry/index'
    }

    void "an invalid entry shows the page again with its errors"() {
        when:
        request.method = 'POST'
        params.name = ''
        params.message = 'hello'
        controller.save()

        then:
        Entry.count() == 0
        view == '/entry/index'
        model.entry.errors.hasFieldErrors('name')
    }
}
