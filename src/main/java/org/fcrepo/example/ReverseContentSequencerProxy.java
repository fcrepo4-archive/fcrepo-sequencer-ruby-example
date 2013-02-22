package org.fcrepo.example;

import javax.jcr.Node;
import javax.jcr.Property;

import org.jruby.embed.PathType;
import org.jruby.embed.ScriptingContainer;
import org.modeshape.jcr.api.sequencer.Sequencer;

/**
 * A Sequencer proxy for reverse_content_sequencer.rb.
 * 
 *
 */
public class ReverseContentSequencerProxy extends Sequencer {

    private Sequencer proxy;

    public ReverseContentSequencerProxy() {
        String filename = "reverse_content_sequencer.rb";

        ScriptingContainer container = new ScriptingContainer();
        container.runScriptlet(PathType.CLASSPATH, filename);

        // Instantiate the JRuby class, and cast the result of eval.
        proxy =
                (Sequencer) container
                        .runScriptlet("ReverseContentSequencer.new");
    }

    @Override
    public boolean execute(Property inputProperty, Node outputNode,
            Context context) throws Exception {
        return proxy.execute(inputProperty, outputNode, context);
    }
}
