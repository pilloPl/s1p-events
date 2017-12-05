package com.s1p.eventsourcing

import org.springframework.kafka.core.KafkaTemplate
import spock.lang.Specification

class RepositoryTest extends Specification {

    KafkaTemplate kafkaTemplate = Stub(KafkaTemplate)
    Repository repository = new Repository(kafkaTemplate)

    def 'should be able to save and load credit card'() {
        given:
            UUID uuid = UUID.randomUUID()
        and:
            CreditCard card = new CreditCard(uuid)
        and:
            card.assignLimit(100)
        and:
            repository.save(card)
        when:
            CreditCard loaded = repository.load(uuid)
        then:
            loaded.availableLimit() == 100
    }
}
