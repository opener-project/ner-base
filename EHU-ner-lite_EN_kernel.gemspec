# -*- encoding: utf-8 -*-
lib = File.expand_path('../lib', __FILE__)
$LOAD_PATH.unshift(lib) unless $LOAD_PATH.include?(lib)

require 'EHU-ner-lite_EN_kernel'

Gem::Specification.new do |gem|
  gem.name          = "EHU-ner-lite_EN_kernel"
  gem.version       = Opener::Kernel::EHU::NER::Lite::EN::VERSION
  gem.authors       = ["sb-olr","sparkboxx"]
  gem.email         = ["sujit@olery.com", "wilco@olery.com"]
  gem.description   = %q{NER kernel for italian lite version}
  gem.summary       = %q{Use this gem in a component}
  gem.homepage      = "http://opener-project.github.com/"

  gem.files         = `git ls-files`.split($/)
  gem.executables   = gem.files.grep(%r{^bin/}).map{ |f| File.basename(f) }
  gem.test_files    = gem.files.grep(%r{^(test|spec|features)/})
  gem.require_paths = ["lib"]
  gem.bindir        = 'bin'

  gem.add_development_dependency 'rspec'
  gem.add_development_dependency 'cucumber'

end
