require 'tempfile'

module Opener
  module Kernel
    module EHU
      module NER
        module Lite
          class EN 
            VERSION = "0.0.2"

            attr_reader :kernel, :lib

            def initialize
              core_dir    = File.expand_path("../core", File.dirname(__FILE__))

              @kernel      = core_dir+'/ehu-opennlp-nerc-en-1.0.jar'
              @lib         = core_dir+'/lib/'
            end

            def command(opts={})
              arguments = opts[:arguments] || []
              arguments << "-n" if opts[:test]
#`cat #{@input} | java -jar #{KERNEL_CORE} -stdin > #{@output}`
              #"java -jar #{kernel} #{opts[:input]} #{arguments.join(' ')}"
              "cat #{opts[:input]} | java -jar #{kernel} -stdin #{arguments.join(' ')}"

            end

          end
        end
      end
    end
  end
end


