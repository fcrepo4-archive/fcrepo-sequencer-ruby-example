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