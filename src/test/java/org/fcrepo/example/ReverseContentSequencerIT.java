package org.fcrepo.example;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * Integration test for ReverseContentSequencer that brings up a repository
 * instance.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/spring-test/master.xml"})
public class ReverseContentSequencerIT {

    /**
     * The injected repository instance.
     */
    @Inject
    private Repository repository;

    /**
     * Tests that when we add a {@Node}, and set a property, our sequencer
     * adds a "reversed-content" node that has the text reversed.
     * 
     * @throws Exception
     */
    @Test
    public final void test() throws Exception {

        Session session = repository.login();
        Node root = session.getRootNode().addNode("jruby");
        Node n = root.addNode("should-be-decorated.example");
        n.addNode("jcr:content", "nt:resource").setProperty("jcr:data", "asdf");

        session.save();
        session.logout();

        // HA!
        sleep(10000);

        session = repository.login();

        Node ni =
                session.getRootNode().getNode("jruby").getNode(
                        "should-be-decorated.example").getNode(
                        "reversed-content");

        assertEquals(ni.getProperty("jcr:data").getString(), "fdsa");
    }

}
