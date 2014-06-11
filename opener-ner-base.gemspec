require File.expand_path('../lib/opener/ners/base/version', __FILE__)

Gem::Specification.new do |gem|
  gem.name          = 'opener-ner-base'
  gem.version       = Opener::Ners::Base::VERSION
  gem.authors       = ['development@olery.com']
  gem.summary       = 'Base NER component for languages such as English.'
  gem.description   = gem.summary
  gem.homepage      = 'http://opener-project.github.com/'

  gem.files = Dir.glob([
    'core/target/ehu-nerc-*.jar',
    'lib/**/*',
    '*.gemspec',
    'README.md'
  ]).select { |file| File.file?(file) }

  gem.executables = Dir.glob('bin/*').map { |file| File.basename(file) }

  gem.add_dependency 'opener-build-tools'

  gem.add_development_dependency 'rspec', '~> 3.0'
  gem.add_development_dependency 'cucumber'
  gem.add_development_dependency 'rake'
end
