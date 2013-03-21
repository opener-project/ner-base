Dir[File.dirname(__FILE__) + '/../../lib/*.rb'].each {|file| require file }
require 'rspec/expectations'
require 'tempfile'
require 'pry'

def kernel_root
  File.expand_path("../../../", __FILE__)
end

def kernel
  Opener::Kernel::EHU::NER::Lite::EN.new
end
