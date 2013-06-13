require 'bundler/gem_tasks'
require 'opener/build-tools'
require 'opener/build-tools/tasks/java'
require 'opener/build-tools/tasks/clean'

include Opener::BuildTools::Requirements
include Opener::BuildTools::Java

# Path to the directory containing all the Java source code.
CORE_DIRECTORY = File.expand_path('../core', __FILE__)

# Path to the local tmp/ directory.
TMP_DIRECTORY = File.expand_path('../tmp', __FILE__)

desc 'Lists all the files of the Gem'
task :files do
  gemspec = Gem::Specification.load('ner-base.gemspec')

  puts gemspec.files.sort
end

desc 'Verifies the requirements'
task :requirements do
  require_executable('java')
  require_version('java', java_version, '1.7')
  require_executable('mvn')
end

namespace :java do
  task :compile => :requirements
end

desc 'Runs the tests'
task :test => 'java:compile' do
  sh 'cucumber features'
end

desc 'Cleans the repository'
task :clean => ['java:clean:packages', 'clean:tmp']

task :build   => 'java:compile'
task :default => :test
