package com.s1p.eventsourcing

import spock.lang.Specification

class CreditCardTest extends Specification {

    CreditCard card = new CreditCard(UUID.randomUUID())

    def 'cannot assign limit for the second time'() {
        given:
            card.assignLimit(100)
        when:
            card.assignLimit(50)
        then:
            thrown(IllegalStateException)
    }

    def 'can assign limit to a card'() {
        when:
            card.assignLimit(100)
        then:
            card.availableLimit() == 100
    }

    def 'cannot withdraw when not enough money'() {
        given:
            card.assignLimit(100)
        when:
            card.withdraw(150)
        then:
            thrown(IllegalStateException)
    }


    def 'can withdraw from a card'() {
        given:
            card.assignLimit(100)
        when:
            card.withdraw(50)
        then:
            card.availableLimit() == 50
    }

    def 'can repay a card'() {
        given:
            card.assignLimit(100)
        and:
            card.withdraw(50)
        when:
            card.repay(50)
        then:
            card.availableLimit() == 100
    }


}
