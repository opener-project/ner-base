require 'open3'
require 'stringio'
require 'nokogiri'
require 'opener/core'

require_relative 'base/version'

require File.expand_path("../../../../core/target/ixa-pipe-nerc-1.5.2.jar", __FILE__)

module Opener
  module Ners
    ##
    # Base NER class that supports various languages such as Dutch and English.
    #
    class Base
      # The default models directory.
      MODELS_PATH = File.expand_path('../../../../models', __FILE__)

      # @return [String]
      attr_reader :models

      # @return [TrueClass|FalseClass]
      attr_reader :enable_time

      ##
      # @param [Hash] options
      #
      # @option options [TrueClass|FalseClass] :enable_time Whether or not to
      #  enable dynamic timestamps (enabled by default).
      #
      def initialize(options = {})
        @models = ENV['NER_BASE_MODELS_PATH'] || MODELS_PATH

        @enable_time = options.fetch(:enable_time, true)
      end

      ##
      # Runs the command and returns the output of STDOUT, STDERR and the
      # process information.
      #
      # @param [String] input The input to process.
      # @return [Array]
      #
      def run(input)
        lang  = language_from_kaf(input)
        model = File.join(models, "#{lang}.bin")

        raise(Core::UnsupportedLanguageError, lang) unless File.file?(model)

        kaf        = new_kaf_document(input)
        properties = build_properties(lang, model)
        annotator  = Java::eus.ixa.ixa.pipe.nerc.Annotate.new(properties)

        annotator.annotate_kaf(enable_time, kaf)
      end

      ##
      # @param [String] input The input KAF document as a string.
      # @return [Java::ixa.kaflib.KAFDocument]
      #
      def new_kaf_document(input)
        input_io = StringIO.new(input)
        reader   = Java::java.io.InputStreamReader.new(input_io.to_inputstream)

        Java::ixa.kaflib.KAFDocument.create_from_stream(reader)
      end

      ##
      # Returns the language for the given KAF document.
      #
      # @param [String] input
      # @return [String]
      #
      def language_from_kaf(input)
        document = Nokogiri::XML(input)
        language = document.at('KAF').attr('xml:lang')

        # Make sure nobody can _somehow_ inject a language such as "../../foo".
        unless language =~ /\A[a-zA-Z\-_]+\z/
          raise Core::UnsupportedLanguageError, language
        end

        language
      end

      private

      # @param [String] language
      # @param [String] model
      def build_properties(language, model)
        properties = Java::java.util.Properties.new

        properties.set_property('language', language)
        properties.set_property('model', model)
        properties.set_property('ruleBasedOption', 'off')
        properties.set_property('dictTag', 'off')
        properties.set_property('dictPath', 'off')
        properties.set_property('clearFeatures', 'no')

        properties
      end
    end # Base
  end # Ners
end # Opener
