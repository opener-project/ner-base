require_relative '../../lib/opener/ner/base'
require 'rspec/expectations'
require 'tempfile'

def kernel_root
  File.expand_path("../../../", __FILE__)
end

def kernel(language)
  return Opener::NER::Base.new(:language => language, :args => ['-t'])
end
