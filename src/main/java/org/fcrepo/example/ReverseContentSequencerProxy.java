
package org.fcrepo.example;

import javax.jcr.Node;
import javax.jcr.Property;

import org.jruby.embed.PathType;
import org.jruby.embed.ScriptingContainer;
import org.modeshape.jcr.api.sequencer.Sequencer;

/**
 * <p>A proxy/wrapper class for reverse_content_sequencer.rb.
 * 
 * <p>This wrapper/proxy business is unfortunately required because:
 * <ol>
 *   <li> Modeshape's Sequencer is presently an abstract class (rather than an 
 *        interface), and 
 *   <li> because jrubyc-generated Java classes extend org.jruby.RubyObject, so 
 *        from ModeShape's perspective, your JRuby class isn't a Sequencer.
 * </ol>
 * 
 * <p>The code is, at least, mostly boilerplate. For your own implementations, 
 * you should only need to change:
 * <ol>
 *   <li> the name of the proxy class (e.g. MySequencerProxy),
 *   <li> the filename (e.g. my_sequencer.rb),
 *   <li> the instantiation of your Ruby class (e.g. "MySequencer.new").
 * </ol>
 *
 */
public class ReverseContentSequencerProxy extends Sequencer {

    /**
     * An instance of ReverseContentSequencer.
     */
    private Sequencer proxy;

    /**
     * Class constructor.
     */
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
    public final boolean execute(final Property inputProperty,
            final Node outputNode, final Context context) throws Exception {
        return proxy.execute(inputProperty, outputNode, context);
    }
}
