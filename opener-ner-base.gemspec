require File.expand_path('../lib/opener/ners/base/version', __FILE__)

Gem::Specification.new do |gem|
  gem.name          = 'opener-ner-base'
  gem.version       = Opener::Ners::Base::VERSION
  gem.authors       = ['development@olery.com']
  gem.summary       = 'Base NER component for languages such as English.'
  gem.description   = gem.summary
  gem.homepage      = 'http://opener-project.github.com/'
  gem.license       = 'Apache 2.0'

  gem.files = Dir.glob([
    'core/target/ixa-pipe-nerc-*.jar',
    'lib/**/*',
    'models/**/*',
    '*.gemspec',
    'README.md',
    'LICENSE.txt'
  ]).select { |file| File.file?(file) }

  gem.executables = Dir.glob('bin/*').map { |file| File.basename(file) }

  gem.add_dependency 'oga'
  gem.add_dependency 'opener-core'

  gem.add_development_dependency 'rspec', '~> 3.0'
  gem.add_development_dependency 'cucumber'
  gem.add_development_dependency 'rake'
  gem.add_development_dependency 'cliver'
end
