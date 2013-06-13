require File.expand_path('../lib/opener/ners/base/version', __FILE__)

generated = Dir.glob('core/ehu-nerc-*.jar')

Gem::Specification.new do |gem|
  gem.name          = 'opener-ner-base'
  gem.version       = Opener::Ners::Base::VERSION
  gem.authors       = ['development@olery.com']
  gem.summary       = 'Base NER component for languages such as English.'
  gem.description   = gem.summary
  gem.homepage      = 'http://opener-project.github.com/'

  gem.files         = (`git ls-files`.split("\n") + generated).sort
  gem.executables   = gem.files.grep(%r{^bin/}).map{ |f| File.basename(f) }
  gem.test_files    = gem.files.grep(%r{^(test|spec|features)/})

  gem.add_development_dependency 'rspec'
  gem.add_development_dependency 'cucumber'
  gem.add_development_dependency 'rake'
end
