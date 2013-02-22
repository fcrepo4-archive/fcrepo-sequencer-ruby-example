# fcrepo-sequencer-ruby-example

[![Build Status](https://travis-ci.org/futures/fcrepo-sequencer-ruby-example.png?branch=master)](https://travis-ci.org/futures/fcrepo-sequencer-ruby-example)

An example of a fcrepo4 [Sequencer](https://docs.jboss.org/author/display/MODE/Sequencing) written in Ruby.

### Roll your own JRuby sequencer

1. Provide a Ruby sequencer class that extends [Sequencer](http://docs.jboss.org/modeshape/3.1.2.Final/api/org/modeshape/jcr/api/sequencer/Sequencer.html) in `src/main/ruby`, e.g.:

  ```ruby
    require 'java'
  
    java_import 'org.modeshape.jcr.api.sequencer.Sequencer'
    java_import 'org.modeshape.jcr.api.JcrTools'
    java_package 'org.fcrepo.example'
  
    class ReverseContentSequencer < Sequencer
      def execute property, outputNode, context
        n = outputNode.addNode("reversed-content", "nt:resource")
        n.setProperty("jcr:data", property.getString().reverse )
        return true
      end
    end
  ```

2. Provide a Java wrapper/proxy class for your Ruby sequencer, e.g.:

  ```java
  package org.fcrepo.example;

  import javax.jcr.Node;
  import javax.jcr.Property;

  import org.jruby.embed.PathType;
  import org.jruby.embed.ScriptingContainer;
  import org.modeshape.jcr.api.sequencer.Sequencer;

  public class ReverseContentSequencerProxy extends Sequencer {

      private Sequencer proxy;

      public ReverseContentSequencerProxy() {
          String filename = "reverse_content_sequencer.rb";

          ScriptingContainer container = new ScriptingContainer();
          container.runScriptlet(PathType.CLASSPATH, filename);

          // Instantiate the JRuby class, and cast the result of eval.
          proxy = (Sequencer) container.runScriptlet("ReverseContentSequencer.new");
      }

      @Override
      public boolean execute(Property inputProperty, Node outputNode,
              Context context) throws Exception {
          return proxy.execute(inputProperty, outputNode, context);
      }
  }
  ```

  This wrapper/proxy business is unfortunately required because 
  a) Modeshape's `Sequencer` is presently an abstract class (rather than an interface), and 
  b) because jrubyc-generated Java classes extend `org.jruby.RubyObject`, so from ModeShape's 
  perspective, your JRuby class isn't a `Sequencer`.

### Testing and building

To run the integration test, which will spin up an instance of Modeshape:

```bash
$ mvn verify
```

To build and install:

```bash
$ mvn install
```
  
This will produce `fcrepo-sequencer-ruby-example-<VERSION>.jar`, which you can then deploy 
to the fcrepo4 webapp (e.g. add to to exploded war's WEB-INF/lib directory or otherwise 
make it available on the webapp's classpath).
