require_relative '../../lib/opener/ners/base'
require 'rspec/expectations'
require 'tempfile'

def kernel_root
  File.expand_path("../../../", __FILE__)
end

def kernel(language)
  return Opener::Ners::Base.new(:language => language, :args => ['-t'])
end
