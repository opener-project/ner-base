require 'open3'
require 'stringio'
require 'nokogiri'

require_relative 'base/version'

require File.expand_path("../../../../core/target/ixa-pipe-nerc-1.5.2.jar", __FILE__)

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
    # @!attribute [r] enable_time
    #  @return [TrueClass|FalseClass]
    #
    class Base
      attr_reader :features, :beamsize, :dictionaries, :dictionaries_path,
        :lexer, :model, :enable_time

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
      # @option options [TrueClass|FalseClass] :enable_time Whether or not to
      #  enable dynamic timestamps (enabled by default).
      #
      def initialize(options = {})
        @dictionaries      = options[:dictionaries]
        @dictionaries_path = options[:dictionaries_path]
        @lexer             = options[:lexer]
        @model             = options.fetch(:model, 'default')
        @enable_time       = options.fetch(:enable_time, true)
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

        properties = Java::java.util.Properties.new

        properties.set_property('model', model)
        properties.set_property('language', lang)

        if lexer
          properties.set_property('ruleBasedOption', lexer)
        end

        if use_dictionaries?
          properties.set_property('dictTag', dictionaries)
          properties.set_property('dictPath', dictionaries_path)
        end

        annotator = Java::eus.ixa.ixa.pipe.nerc.Annotate.new(properties)

        annotator.annotate_kaf(enable_time, kaf)

        return kaf.to_string
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
        return dictionaries || dictionaries_path || features == 'dict'
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
