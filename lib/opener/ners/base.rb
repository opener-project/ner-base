require 'open3'
require 'java'
require 'stringio'

require 'core/target/ehu-nerc-1.0.jar'
import 'ehu.nerc.Annotate'
import 'ixa.kaflib.KAFDocument'
import 'java.io.InputStreamReader'

require_relative 'base/version'

module Opener
  module Ners
    ##
    # Base NER class that supports various languages such as Dutch and English.
    #
    # @!attribute [r] args
    #  @return [Array]
    # @!attribute [r] options
    #  @return [Hash]
    #
    class Base
      attr_reader :args, :options

      ##
      # @param [Hash] options
      #
      # @option options [Array] :args The commandline arguments to pass to the
      #  underlying Java code.
      #
      def initialize(options = {})
        @args    = options.delete(:args) || []
        @options = options
      end

      ##
      # Runs the command and returns the output of STDOUT, STDERR and the
      # process information.
      #
      # @param [String] input The input to process.
      # @return [Array]
      #
      def run(input)
        input     = StringIO.new(input) unless input.kind_of?(IO)
        annotator = Annotate.new(language)
        reader    = InputStreamReader.new(input.to_inputstream)
        kaf       = KAFDocument.create_from_stream(reader)

        kaf.add_linguistic_processor("entities","ehu-nerc-"+language,"now","1.0")
        annotator.annotateNEsToKAF(kaf)

        return kaf.to_string
      end

      ##
      # @return [String]
      #
      def language
        return options[:language]
      end

      protected

      ##
      # @return [String]
      #
      def core_dir
        return File.expand_path('../../../../core/target', __FILE__)
      end

      ##
      # @return [String]
      #
      def kernel
        return File.join(core_dir, 'ehu-nerc-1.0.jar')
      end
    end # Base
  end # Ners
end # Opener
