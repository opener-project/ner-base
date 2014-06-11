desc 'Verifies the requirements'
task :requirements do
  require 'cliver'

  Cliver.detect!('java', '~> 1.7', :detector => '-version')
  Cliver.detect!('mvn', '~> 3.0', :detector => '-version')
end
