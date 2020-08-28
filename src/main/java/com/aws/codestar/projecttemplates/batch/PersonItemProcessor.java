package com.aws.codestar.projecttemplates.batch;

import com.aws.codestar.projecttemplates.entity.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {

    private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);

    @Override
    public Person process(final Person person) throws Exception {
        final String firstName = person.getFirstName().toUpperCase();

        final Person transformedPerson = new Person(firstName, "Proecessed by: " + java.net.InetAddress.getLocalHost().getHostName());

        log.info("Converting (" + person + ") into (" + transformedPerson + ")");

        return transformedPerson;
    }

}