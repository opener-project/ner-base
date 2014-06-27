require 'open3'
require 'stringio'
require 'nokogiri'

require File.expand_path("../../../../core/target/ixa-pipe-nerc-1.1.0.jar", __FILE__)

require_relative 'base/version'

module Opener
  module Ners
    ##
    # Base NER class that supports various languages such as Dutch and English.
    #
    # @!attribute [r] options
    #  @return [Hash]
    #
    # @!attribute [r] features
    #  @return [String]
    #
    # @!attribute [r] beamsize
    #  @return [Fixnum]
    #
    # @!attribute [r] dictionaries
    #  @return [String]
    #
    # @!attribute [r] dictionaries_path
    #  @return [String]
    #
    # @!attribute [r] lexer
    #  @return [Fixnum]
    #
    # @!attribute [r] model
    #  @return [String]
    #
    class Base
      attr_reader :features, :beamsize, :dictionaries, :dictionaries_path,
        :lexer, :model

      ##
      # @param [Hash] options
      #
      # @option options [String] :features The NERC feature to use, defaults to
      #  "baseline".
      #
      # @option options [Fixnum] :beamsize The beam size for decoding, defaults
      #  to 3.
      #
      # @option options [String] :dictionaries The dictionary to use, if any.
      #
      # @option options [String] :dictionaries_path The path to the
      #  dictionaries.
      #
      # @option options [Fixnum] :lexer The lexer rules to use for NERC
      #  tagging.
      #
      # @option options [String] :model The model to use for NERC annotation.
      #
      def initialize(options = {})
        @dictionaries      = options[:dictionaries]
        @dictionaries_path = options[:dictionaries_path]
        @features          = options[:features] || 'baseline'
        @beamsize          = options[:beamsize] || 3
        @lexer             = options[:lexer]
        @model             = options[:model] || 'default'
      end

      ##
      # Runs the command and returns the output of STDOUT, STDERR and the
      # process information.
      #
      # @param [String] input The input to process.
      # @return [Array]
      #
      def run(input)
        lang = language_from_kaf(input)
        kaf  = new_kaf_document(input)
        args = [lang, model, features, beamsize]

        lp_tag = kaf.add_linguistic_processor(
          'entities',
          'ixa-pipe-nerc-' + lang + '-' + model,
          VERSION
        )

        lp_tag.set_begin_timestamp

        if use_dictionaries?
          args += [dictionaries, dictionaries_path, lexer]
        end

        annotator = Java::es.ehu.si.ixa.pipe.nerc.Annotate.new(*args)

        annotator.annotate_nes_to_kaf(kaf)

        lp_tag.set_end_timestamp

        output = kaf.to_string

        return output
      end

      ##
      # @param [String] input The input KAF document as a string.
      # @return [Java::ixa.kaflib.KAFDocument]
      #
      def new_kaf_document(input)
        input_io = StringIO.new(input)
        reader   = Java::java.io.InputStreamReader.new(input_io.to_inputstream)

        return Java::ixa.kaflib.KAFDocument.create_from_stream(reader)
      end

      ##
      # @return [TrueClass|FalseClass]
      #
      def use_dictionaries?
        return !!dictionaries || !!dictionaries_path || features == 'dict'
      end

      ##
      # Returns the language for the given KAF document.
      #
      # @param [String] input
      # @return [String]
      #
      def language_from_kaf(input)
        document = Nokogiri::XML(input)

        return document.at('KAF').attr('xml:lang')
      end
    end # Base
  end # Ners
end # Opener
