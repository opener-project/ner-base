require 'open3'

require_relative 'base/version'

module Opener
  module NER
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
      #  underlying Python script.
      #
      def initialize(options = {})
        @args    = options.delete(:args) || []
        @options = options
      end

      ##
      # Builds the command used to execute the kernel.
      #
      # @return [String]
      #
      def command
        return "java -jar #{kernel} -l #{language} #{args.join(' ')}"
      end

      ##
      # Runs the command and returns the output of STDOUT, STDERR and the
      # process information.
      #
      # @param [String] input The input to process.
      # @return [Array]
      #
      def run(input)
        unless File.file?(kernel)
          raise "The Java kernel (#{kernel}) does not exist"
        end

        return Open3.capture3(command, :stdin_data => input)
      end

      ##
      # Runs the command and takes care of error handling/aborting based on the
      # output.
      #
      # @see #run
      #
      def run!(input)
        stdout, stderr, process = run(input)

        if process.success?
          puts stdout

          STDERR.puts(stderr) unless stderr.empty?
        else
          abort stderr
        end
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
        return File.expand_path('../../../../core', __FILE__)
      end

      ##
      # @return [String]
      #
      def kernel
        return File.join(core_dir, 'ehu-nerc-1.0.jar')
      end
    end # Base
  end # NER
end # Opener
