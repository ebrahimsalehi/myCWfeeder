package com.aws.codestar.projecttemplates.batch;

import com.aws.codestar.projecttemplates.entity.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {

    private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);

    @Autowired
    private Environment environment;

    @Override
    public Person process(final Person person) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final String firstName = person.getFirstName().toUpperCase();

        final Person transformedPerson = new Person(firstName, "Processed by: " + environment.getProperty("server.port"));

        log.info("Converting (" + person + ") into (" + transformedPerson + ")");

        return transformedPerson;
    }

}